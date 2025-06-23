package tech.powerscheduler.server.application.assembler

import org.springframework.stereotype.Component
import tech.powerscheduler.server.application.dto.request.WorkflowAddRequestDTO
import tech.powerscheduler.server.application.dto.request.WorkflowEditRequestDTO
import tech.powerscheduler.server.application.dto.request.WorkflowQueryRequestDTO
import tech.powerscheduler.server.application.dto.response.WorkflowQueryResponseDTO
import tech.powerscheduler.server.domain.appgroup.AppGroup
import tech.powerscheduler.server.domain.workflow.Workflow
import tech.powerscheduler.server.domain.workflow.WorkflowQuery

/**
 * @author grayrat
 * @since 2025/6/23
 */
@Component
class WorkflowAssembler {

    fun toDomainQuery(param: WorkflowQueryRequestDTO): WorkflowQuery {
        return WorkflowQuery().apply {
            this.namespaceCode = param.namespaceCode
            this.appCode = param.appCode
            this.name = param.name
        }
    }

    fun toWorkflowQueryResponseDTO(workflow: Workflow): WorkflowQueryResponseDTO {
        return WorkflowQueryResponseDTO()
    }

    fun toDomainModel4AddRequest(appGroup: AppGroup, param: WorkflowAddRequestDTO): Workflow {
        return Workflow().apply {
            this.appGroup = appGroup
            this.name = param.name
            this.description = param.description
            this.scheduleType = param.scheduleType
            this.scheduleConfig = param.scheduleConfig
            this.maxConcurrentNum = param.maxConcurrentNum
            this.retentionPolicy = param.retentionPolicy
            this.retentionValue = param.retentionValue
            this.enabled = false
        }
    }

    fun toDomainModel4EditRequest(workflow: Workflow, param: WorkflowEditRequestDTO): Workflow {
        return Workflow().apply {
            this.appGroup = workflow.appGroup
            this.id = workflow.id
            this.name = param.name
            this.description = param.description
            this.scheduleType = param.scheduleType
            this.scheduleConfig = param.scheduleConfig
            this.maxConcurrentNum = param.maxConcurrentNum
            this.retentionPolicy = param.retentionPolicy
            this.retentionValue = param.retentionValue
            this.enabled = false
            this.lastCompletedAt = null
            this.nextScheduleAt = null
        }
    }

}