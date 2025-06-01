package org.grayrat.powerscheduler.server.application.service

import org.grayrat.powerscheduler.common.dto.request.JobProgressReportRequestDTO
import org.grayrat.powerscheduler.common.dto.request.JobTerminateRequestDTO
import org.grayrat.powerscheduler.common.enums.JobStatusEnum
import org.grayrat.powerscheduler.common.enums.JobStatusEnum.*
import org.grayrat.powerscheduler.common.enums.ScheduleTypeEnum
import org.grayrat.powerscheduler.common.exception.BizException
import org.grayrat.powerscheduler.server.application.assembler.JobInstanceAssembler
import org.grayrat.powerscheduler.server.application.dto.request.JobInstanceQueryRequestDTO
import org.grayrat.powerscheduler.server.application.dto.request.JobRunRequestDTO
import org.grayrat.powerscheduler.server.application.dto.response.JobInstanceDetailResponseDTO
import org.grayrat.powerscheduler.server.application.dto.response.JobInstanceQueryResponseDTO
import org.grayrat.powerscheduler.server.application.dto.response.PageDTO
import org.grayrat.powerscheduler.server.application.utils.toDTO
import org.grayrat.powerscheduler.server.domain.jobinfo.JobId
import org.grayrat.powerscheduler.server.domain.jobinfo.JobInfoRepository
import org.grayrat.powerscheduler.server.domain.jobinstance.JobInstanceId
import org.grayrat.powerscheduler.server.domain.jobinstance.JobInstanceRepository
import org.grayrat.powerscheduler.server.domain.worker.WorkerRemoteService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDateTime

/**
 * 任务实例相关服务
 *
 * @author grayrat
 * @since 2025/4/16
 */
@Service
class JobInstanceService(
    private val jobInfoRepository: JobInfoRepository,
    private val jobInstanceRepository: JobInstanceRepository,
    private val jobInstanceAssembler: JobInstanceAssembler,
    private val transactionTemplate: TransactionTemplate,
    private val workerRemoteService: WorkerRemoteService,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun list(param: JobInstanceQueryRequestDTO): PageDTO<JobInstanceQueryResponseDTO> {
        val query = jobInstanceAssembler.toDomainQuery(param)
        val page = jobInstanceRepository.pageQuery(query)
        return page.toDTO().map { jobInstanceAssembler.toJobInstanceQueryResponseDTO(it) }
    }

    fun detail(jobInstanceId: Long): JobInstanceDetailResponseDTO? {
        val jobInstance = jobInstanceRepository.findById(JobInstanceId(jobInstanceId))
        return jobInstance?.let { jobInstanceAssembler.toJobInstanceQueryDetailDTO(it) }
    }

    fun getErrorMessage(jobInstanceId: Long): String {
        val jobInstance = jobInstanceRepository.findById(JobInstanceId(jobInstanceId))
            ?: throw BizException(message = "重跑任务失败: 任务实例不存在")
        return jobInstance.takeIf { it.jobStatus == FAILED }?.message.orEmpty()
    }

    fun terminate(jobInstanceId: Long) {
        val jobInstance = jobInstanceRepository.findById(JobInstanceId(jobInstanceId))
            ?: throw BizException(message = "终止任务失败: 任务实例不存在")
        when (jobInstance.jobStatus!!) {
            WAITING_DISPATCH -> {
                jobInstance.jobStatus = CANCELED
                jobInstanceRepository.save(jobInstance)
            }

            DISPATCHING, PENDING, PROCESSING -> {
                jobInstance.jobStatus = CANCELED
                jobInstanceRepository.save(jobInstance)
                val terminateParam = JobTerminateRequestDTO().apply {
                    this.jobInstanceId = jobInstanceId
                }
                workerRemoteService.terminate(
                    baseUrl = jobInstance.workerAddress!!,
                    param = terminateParam
                )
            }

            FAILED, SUCCESS, CANCELED -> throw BizException("终止任务失败: 任务已经完成")
        }
    }

    fun run(param: JobRunRequestDTO): Long {
        val jobInfo = jobInfoRepository.findById(JobId(param.jobId!!))
            ?: throw BizException(message = "运行任务失败: 任务不存在")
        val jobInstance = jobInfo.createInstance().apply {
            this.dataTime = param.dataTime
            this.executeParams = param.executeParams
            this.workerAddress = param.workerAddress
            this.maxAttemptCnt = 1
            this.scheduleAt = LocalDateTime.now()
        }
        val jobInstanceId = jobInstanceRepository.save(jobInstance)
        return jobInstanceId.value
    }

    fun reattempt(jobInstanceId: Long): Long {
        val jobInstance = jobInstanceRepository.findById(JobInstanceId(jobInstanceId))
            ?: throw BizException(message = "重跑任务失败: 任务实例不存在")
        val jobInstanceToReattempt = jobInstance.cloneForReattempt()
        val jobInstanceId = jobInstanceRepository.save(jobInstanceToReattempt)
        return jobInstanceId.value
    }

    fun updateProgress(param: JobProgressReportRequestDTO) {
        val jobInstanceId = JobInstanceId(param.jobInstanceId!!)
        val jobInstance = jobInstanceRepository.findById(jobInstanceId)
            ?: throw BizException(message = "更新任务状态失败: 任务实例[$jobInstanceId]不存在")
        if (jobInstance.jobStatus in JobStatusEnum.COMPLETED_STATUSES) {
            log.info("updateProgress cancel, jobInstance [{}] is already completed", jobInstanceId)
            return
        }
        jobInstance.apply {
            this.jobStatus = param.jobStatus
            this.startAt = param.startAt
            this.endAt = param.endAt
            this.message = param.message?.take(5000)
        }
        transactionTemplate.executeWithoutResult {
            if (param.jobStatus == FAILED && jobInstance.canReattempt) {
                jobInstance.resetStatusForReattempt()
            }
            jobInstanceRepository.save(jobInstance)

            if (param.jobStatus in JobStatusEnum.COMPLETED_STATUSES) {
                val jobInfo = jobInfoRepository.lockById(jobInstance.jobId!!)
                if (jobInfo == null) {
                    return@executeWithoutResult
                }
                jobInfo.lastCompletedAt = param.endAt
                if (jobInstance.scheduleType == ScheduleTypeEnum.ONE_TIME) {
                    jobInfo.enabled = false
                }
                if (jobInfo.scheduleType == ScheduleTypeEnum.FIX_DELAY) {
                    jobInfo.updateNextScheduleTime()
                }
                jobInfoRepository.save(jobInfo)
            }
        }
    }
}