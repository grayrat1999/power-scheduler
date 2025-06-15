package tech.powerscheduler.server.application.service

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import tech.powerscheduler.common.enums.ExecuteModeEnum
import tech.powerscheduler.common.enums.JobStatusEnum
import tech.powerscheduler.common.enums.JobStatusEnum.*
import tech.powerscheduler.common.enums.ScheduleTypeEnum
import tech.powerscheduler.common.exception.BizException
import tech.powerscheduler.server.application.assembler.JobInstanceAssembler
import tech.powerscheduler.server.application.assembler.TaskAssembler
import tech.powerscheduler.server.application.dto.request.JobInstanceQueryRequestDTO
import tech.powerscheduler.server.application.dto.request.JobProgressQueryRequestDTO
import tech.powerscheduler.server.application.dto.request.JobRunRequestDTO
import tech.powerscheduler.server.application.dto.response.JobInstanceDetailResponseDTO
import tech.powerscheduler.server.application.dto.response.JobInstanceQueryResponseDTO
import tech.powerscheduler.server.application.dto.response.JobProgressQueryResponseDTO
import tech.powerscheduler.server.application.dto.response.PageDTO
import tech.powerscheduler.server.application.utils.toDTO
import tech.powerscheduler.server.domain.jobinfo.JobId
import tech.powerscheduler.server.domain.jobinfo.JobInfoRepository
import tech.powerscheduler.server.domain.jobinstance.JobInstanceId
import tech.powerscheduler.server.domain.jobinstance.JobInstanceRepository
import tech.powerscheduler.server.domain.jobinstance.JobInstanceTerminatedEvent
import tech.powerscheduler.server.domain.task.TaskRepository
import java.time.LocalDateTime

/**
 * 任务实例相关服务
 *
 * @author grayrat
 * @since 2025/4/16
 */
@Service
class JobInstanceService(
    private val taskAssembler: TaskAssembler,
    private val taskRepository: TaskRepository,
    private val jobInfoRepository: JobInfoRepository,
    private val jobInstanceRepository: JobInstanceRepository,
    private val jobInstanceAssembler: JobInstanceAssembler,
    private val transactionTemplate: TransactionTemplate,
    private val applicationEventPublisher: ApplicationEventPublisher,
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
            WAITING_SCHEDULE, WAITING_DISPATCH, DISPATCHING, PENDING, PROCESSING -> {
                jobInstance.terminate()
                jobInstanceRepository.save(jobInstance)
                val terminatedEvent = JobInstanceTerminatedEvent(
                    jobInstanceId = JobInstanceId(jobInstanceId),
                )
                applicationEventPublisher.publishEvent(terminatedEvent)
            }

            FAILED, SUCCESS, CANCELED -> throw BizException("终止任务失败: 任务已经完成")
            UNKNOWN -> throw BizException("出现未知状态")
        }
    }

    fun run(param: JobRunRequestDTO): Long {
        val jobInfo = jobInfoRepository.findById(JobId(param.jobId!!))
            ?: throw BizException(message = "运行任务失败: 任务不存在")
        val jobInstance = jobInfo.createInstance().apply {
            this.dataTime = param.dataTime
            this.workerAddress = param.workerAddress
            this.executeParams = param.executeParams
            this.maxAttemptCnt = 0
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

    fun queryProgress(param: JobProgressQueryRequestDTO): PageDTO<JobProgressQueryResponseDTO> {
        val jobInstanceId = JobInstanceId(param.jobInstanceId!!)
        val jobInstance = jobInstanceRepository.findById(jobInstanceId) ?: return PageDTO.empty()
        val batch = jobInstance.batch!!
        val pageQuery = param.toDomainQuery()
        val page = taskRepository.findAllByJobInstanceIdAndBatch(
            jobInstanceId = jobInstanceId,
            batch = batch,
            pageQuery = pageQuery
        )
        return page.toDTO().map { taskAssembler.toJobProgressQueryResponseDTO(it) }
    }

    fun updateJobInstanceProgress(jobInstanceId: JobInstanceId) {
        val jobInstance = jobInstanceRepository.findById(jobInstanceId)
        if (jobInstance == null) {
            log.warn("更新任务状态失败: 任务实例[${jobInstanceId.value}]不存在")
            return
        }
        if (jobInstance.jobStatus in JobStatusEnum.COMPLETED_STATUSES) {
            log.info("updateProgress cancel, jobInstance [{}] is [{}]", jobInstanceId.value, jobInstance.jobStatus)
            return
        }
        val tasks = taskRepository.findAllByJobInstanceId(jobInstanceId)
        val calculatedJobStatus = jobInstance.calculateJobStatus(tasks)
        // 如果计算出的任务状态与当前一样, 则不需要更新状态
        if (jobInstance.jobStatus == calculatedJobStatus) {
            return
        }
        jobInstance.apply {
            this.jobStatus = calculatedJobStatus
            // task可能会失败重试, 开始时间只取第一个task的开始时间
            if (this.startAt == null) {
                this.startAt = this.calculateStartAt(tasks)
            }
            this.workerAddress = this.calculateWorkerAddress(tasks)
            if (calculatedJobStatus in JobStatusEnum.COMPLETED_STATUSES) {
                this.endAt = this.calculateEndAt(tasks)
            }
            if (calculatedJobStatus == FAILED) {
                if (this.canReattempt) {
                    this.resetStatusForReattempt()
                } else {
                    if (this.executeMode == ExecuteModeEnum.SINGLE) {
                        this.message = tasks.mapNotNull { it.message }.firstOrNull { it.isNotBlank() }
                    }
                }
            }
        }
        transactionTemplate.executeWithoutResult {
            jobInstanceRepository.save(jobInstance)
            if (jobInstance.jobStatus in JobStatusEnum.COMPLETED_STATUSES) {
                val jobInfo = jobInfoRepository.lockById(jobInstance.jobId!!)
                if (jobInfo == null) {
                    return@executeWithoutResult
                }
                jobInfo.lastCompletedAt = jobInstance.endAt
                if (jobInstance.scheduleType == ScheduleTypeEnum.ONE_TIME) {
                    jobInfo.enabled = false
                }
                if (jobInfo.scheduleType == ScheduleTypeEnum.FIX_DELAY) {
                    jobInfo.updateNextScheduleTime()
                }
                jobInfoRepository.save(jobInfo)
            }
        }
        log.info("jobInstance update successfully: id={}, status={}", jobInstanceId.value, jobInstance.jobStatus)
    }
}