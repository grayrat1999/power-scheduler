package tech.powerscheduler.server.application.assembler

import org.springframework.stereotype.Component
import tech.powerscheduler.server.application.dto.request.JobInstanceQueryRequestDTO
import tech.powerscheduler.server.application.dto.response.JobInstanceDetailResponseDTO
import tech.powerscheduler.server.application.dto.response.JobInstanceQueryResponseDTO
import tech.powerscheduler.server.application.utils.toDTO
import tech.powerscheduler.server.domain.job.JobInstance
import tech.powerscheduler.server.domain.job.JobInstanceQuery

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
            this.namespaceCode = param.namespaceCode
            this.appCode = param.appCode
            this.jobId = param.jobId
            this.jobInstanceId = param.jobInstanceId
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
            this.workerAddress = domainModel.workerAddress
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

}