package tech.powerscheduler.server.application.assembler

import org.springframework.stereotype.Component
import tech.powerscheduler.common.dto.request.JobDispatchRequestDTO
import tech.powerscheduler.server.domain.task.Task

/**
 * @author grayrat
 * @since 2025/6/7
 */
@Component
class TaskAssembler {

    fun toDispatchTaskRequestDTO(domainModel: Task): JobDispatchRequestDTO {
        return JobDispatchRequestDTO().also {
            it.jobId = domainModel.jobId!!.value
            it.jobInstanceId = domainModel.jobInstanceId!!.value
            it.taskId = domainModel.id!!.value
            it.appCode = domainModel.appCode
            it.jobName = domainModel.jobName
            it.jobType = domainModel.jobType
            it.processor = domainModel.processor
            it.jobStatus = domainModel.jobStatus
            it.executeParams = domainModel.executeParams
            it.scheduleAt = domainModel.scheduleAt
            it.executeMode = domainModel.executeMode
            it.dataTime = domainModel.dataTime
            it.scriptType = domainModel.scriptType
            it.scriptCode = domainModel.scriptCode
            it.attemptCnt = domainModel.attemptCnt
            it.priority = domainModel.priority ?: 0
        }
    }

}