package tech.powerscheduler.server.application.assembler

import org.springframework.stereotype.Component
import tech.powerscheduler.common.dto.request.JobDispatchRequestDTO
import tech.powerscheduler.server.application.dto.request.JobInstanceQueryRequestDTO
import tech.powerscheduler.server.application.dto.response.JobInstanceDetailResponseDTO
import tech.powerscheduler.server.application.dto.response.JobInstanceQueryResponseDTO
import tech.powerscheduler.server.application.utils.toDTO
import tech.powerscheduler.server.domain.jobinstance.JobInstance
import tech.powerscheduler.server.domain.jobinstance.JobInstanceQuery

/**
 * @author grayrat
 * @since 2025/4/27
 */
@Component
class JobInstanceAssembler {

    fun toDomainQuery(param: JobInstanceQueryRequestDTO): JobInstanceQuery {
        return JobInstanceQuery().apply {
            this.pageNo = param.pageNo
            this.pageSize = param.pageSize
            this.jobId = param.jobId
            this.jobInstanceId = param.jobInstanceId
            this.appCode = param.appCode
            this.jobName = param.jobName
            this.jobStatus = param.jobStatus
            this.startAtRange = param.startAtRange
            this.endAtRange = param.endAtRange
        }
    }

    fun toJobInstanceQueryResponseDTO(domainModel: JobInstance): JobInstanceQueryResponseDTO {
        return JobInstanceQueryResponseDTO().apply {
            this.id = domainModel.id!!.value
            this.jobId = domainModel.jobId?.value
            this.appCode = domainModel.appGroup?.code
            this.appName = domainModel.appGroup?.name
            this.jobName = domainModel.jobName
            this.jobType = domainModel.jobType.toDTO()
            this.processor = domainModel.processor
            this.jobStatus = domainModel.jobStatus.toDTO()
            this.scheduleAt = domainModel.scheduleAt
            this.startAt = domainModel.startAt
            this.endAt = domainModel.endAt
            this.executeParams = domainModel.executeParams
            this.executeMode = domainModel.executeMode.toDTO()
            this.scheduleType = domainModel.scheduleType.toDTO()
            this.message = domainModel.message
            this.dataTime = domainModel.dataTime
            this.scriptType = domainModel.scriptType.toDTO()
            this.scriptCode = domainModel.scriptCode
            this.attemptCnt = domainModel.attemptCnt
            this.priority = domainModel.priority
            this.createdBy = domainModel.createdBy
            this.createdAt = domainModel.createdAt
            this.updatedBy = domainModel.updatedBy
            this.updatedAt = domainModel.updatedAt
        }
    }

    fun toJobInstanceQueryDetailDTO(domainModel: JobInstance): JobInstanceDetailResponseDTO {
        return JobInstanceDetailResponseDTO().apply {
            this.id = domainModel.id!!.value
            this.jobId = domainModel.jobId?.value
            this.appCode = domainModel.appGroup?.code
            this.appName = domainModel.appGroup?.name
            this.schedulerAddress = domainModel.schedulerAddress
            this.jobName = domainModel.jobName
            this.jobType = domainModel.jobType.toDTO()
            this.processor = domainModel.processor
            this.jobStatus = domainModel.jobStatus.toDTO()
            this.scheduleAt = domainModel.scheduleAt
            this.startAt = domainModel.startAt
            this.endAt = domainModel.endAt
            this.executeParams = domainModel.executeParams
            this.executeMode = domainModel.executeMode.toDTO()
            this.scheduleType = domainModel.scheduleType.toDTO()
            this.message = domainModel.message
            this.dataTime = domainModel.dataTime
            this.scriptType = domainModel.scriptType.toDTO()
            this.scriptCode = domainModel.scriptCode
            this.attemptCnt = domainModel.attemptCnt
            this.priority = domainModel.priority
        }
    }

    fun toDispatchJobRequestDTO(domainModel: JobInstance): JobDispatchRequestDTO {
        return JobDispatchRequestDTO().also {
            it.jobId = domainModel.jobId!!.value
            it.jobInstanceId = domainModel.id!!.value
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