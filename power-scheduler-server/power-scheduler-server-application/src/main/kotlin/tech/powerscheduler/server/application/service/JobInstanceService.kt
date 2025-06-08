package tech.powerscheduler.server.application.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import tech.powerscheduler.common.dto.request.JobProgressReportRequestDTO
import tech.powerscheduler.common.dto.request.JobTerminateRequestDTO
import tech.powerscheduler.common.enums.JobStatusEnum
import tech.powerscheduler.common.enums.JobStatusEnum.*
import tech.powerscheduler.common.enums.ScheduleTypeEnum
import tech.powerscheduler.common.exception.BizException
import tech.powerscheduler.server.application.assembler.JobInstanceAssembler
import tech.powerscheduler.server.application.dto.request.JobInstanceQueryRequestDTO
import tech.powerscheduler.server.application.dto.request.JobRunRequestDTO
import tech.powerscheduler.server.application.dto.response.JobInstanceDetailResponseDTO
import tech.powerscheduler.server.application.dto.response.JobInstanceQueryResponseDTO
import tech.powerscheduler.server.application.dto.response.PageDTO
import tech.powerscheduler.server.application.utils.toDTO
import tech.powerscheduler.server.domain.jobinfo.JobId
import tech.powerscheduler.server.domain.jobinfo.JobInfoRepository
import tech.powerscheduler.server.domain.jobinstance.JobInstanceId
import tech.powerscheduler.server.domain.jobinstance.JobInstanceRepository
import tech.powerscheduler.server.domain.task.TaskId
import tech.powerscheduler.server.domain.task.TaskRepository
import tech.powerscheduler.server.domain.worker.WorkerRemoteService
import java.time.LocalDateTime

/**
 * 任务实例相关服务
 *
 * @author grayrat
 * @since 2025/4/16
 */
@Service
class JobInstanceService(
    private val taskRepository: TaskRepository,
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
        if (jobInstance.startAt == null) {
            jobInstance.startAt = LocalDateTime.now()
        }
        if (jobInstance.endAt == null) {
            jobInstance.endAt = LocalDateTime.now()
        }
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
        val taskId = TaskId(param.taskId!!)
        val task = taskRepository.findById(taskId)
        if (task == null) {
            log.warn("更新任务状态失败: 子任务[${taskId.value}]不存在")
            return
        }
        if (task.jobStatus in JobStatusEnum.COMPLETED_STATUSES) {
            log.info("updateProgress cancel, jobInstance [{}] is already completed", taskId.value)
            return
        }
        task.apply {
            this.jobStatus = param.jobStatus
            this.startAt = param.startAt
            this.endAt = param.endAt
            this.message = param.message?.take(5000)
        }

        transactionTemplate.executeWithoutResult {
            if (param.jobStatus == FAILED && task.canReattempt) {
                task.resetStatusForReattempt()
            }
            taskRepository.save(task)

            if (task.jobStatus in JobStatusEnum.COMPLETED_STATUSES) {
                updateJobInstanceProgress(task.jobInstanceId!!)
            }
        }
    }

    fun updateJobInstanceProgress(jobInstanceId: JobInstanceId) {
        val jobInstance = jobInstanceRepository.findById(jobInstanceId)
        if (jobInstance == null) {
            log.warn("更新任务状态失败: 任务实例[${jobInstanceId.value}]不存在")
            return
        }
        if (jobInstance.jobStatus in JobStatusEnum.COMPLETED_STATUSES) {
            log.info("updateProgress cancel, jobInstance [{}] is already completed", jobInstanceId.value)
            return
        }
        val tasks = taskRepository.findAllByJobInstanceId(jobInstanceId)
        val calculatedJobStatus = jobInstance.calculateJobStatus(tasks)
        // 如果计算出的任务状态与当前一样, 则不需要更新状态
        if (jobInstance.jobStatus == calculatedJobStatus) {
            return
        }
        jobInstance.apply {
            if (calculatedJobStatus == FAILED) {
                if (this.canReattempt) {
                    this.resetStatusForReattempt()
                } else {
                    this.endAt = LocalDateTime.now()
                    this.jobStatus = FAILED
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
                    jobInfo.updateNextScheduleTime(now = LocalDateTime.now())
                }
                jobInfoRepository.save(jobInfo)
            }
        }
    }
}