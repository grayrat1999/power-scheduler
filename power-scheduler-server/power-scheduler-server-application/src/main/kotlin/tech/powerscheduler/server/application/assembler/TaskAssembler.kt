package tech.powerscheduler.server.application.assembler

import org.springframework.stereotype.Component
import tech.powerscheduler.common.dto.request.JobDispatchRequestDTO
import tech.powerscheduler.common.dto.response.FetchTaskResultResponseDTO
import tech.powerscheduler.common.enums.TaskTypeEnum
import tech.powerscheduler.server.application.dto.response.JobProgressQueryResponseDTO
import tech.powerscheduler.server.application.utils.toDTO
import tech.powerscheduler.server.domain.task.Task

/**
 * @author grayrat
 * @since 2025/6/7
 */
@Component
class TaskAssembler {

    fun toTaskDispatchRequestDTO(domainModel: Task): JobDispatchRequestDTO {
        return JobDispatchRequestDTO().also {
            it.jobId = domainModel.jobId!!.value
            it.jobInstanceId = domainModel.jobInstanceId!!.value
            it.taskId = domainModel.id!!.value
            it.taskName = domainModel.taskName
            it.parentTaskId = domainModel.parentId?.value
            it.appCode = domainModel.appCode
            it.jobType = domainModel.jobType
            it.processor = domainModel.processor
            it.jobStatus = domainModel.taskStatus
            it.executeParams = domainModel.executeParams
            it.scheduleAt = domainModel.scheduleAt
            it.executeMode = domainModel.executeMode
            it.dataTime = domainModel.dataTime
            it.scriptType = domainModel.scriptType
            it.scriptCode = domainModel.scriptCode
            it.attemptCnt = domainModel.attemptCnt
            it.priority = domainModel.priority ?: 0
            it.taskType = domainModel.taskType
            if (it.taskType == TaskTypeEnum.SUB) {
                it.taskBody = domainModel.taskBody
            }
        }
    }

    fun toJobProgressQueryResponseDTO(task: Task): JobProgressQueryResponseDTO {
        return JobProgressQueryResponseDTO().also {
            it.taskId = task.id!!.value
            it.jobInstanceId = task.jobInstanceId!!.value
            it.taskName = task.taskName
            it.taskStatus = task.taskStatus.toDTO()
            it.workerAddress = task.workerAddress
            it.startAt = task.startAt
            it.endAt = task.endAt
        }
    }

    fun toFetchTaskResultResponseDTO(task: Task): FetchTaskResultResponseDTO {
        return FetchTaskResultResponseDTO().also {
            it.taskId = task.id!!.value
            it.taskName = task.taskName
            it.result = task.result
        }
    }
}