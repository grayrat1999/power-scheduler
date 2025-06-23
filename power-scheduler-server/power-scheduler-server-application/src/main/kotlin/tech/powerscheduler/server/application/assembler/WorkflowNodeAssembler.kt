package tech.powerscheduler.server.application.assembler

import org.springframework.stereotype.Component
import tech.powerscheduler.server.application.dto.request.WorkflowNodeAddRequestDTO
import tech.powerscheduler.server.application.dto.request.WorkflowNodeEditRequestDTO
import tech.powerscheduler.server.application.dto.response.WorkflowNodeQueryResponseDTO
import tech.powerscheduler.server.domain.workflow.Workflow
import tech.powerscheduler.server.domain.workflow.WorkflowNode

/**
 * @author grayrat
 * @since 2025/6/23
 */
@Component
class WorkflowNodeAssembler {

    fun toWorkflowNodeQueryResponseDTO(workflowNode: WorkflowNode): WorkflowNodeQueryResponseDTO {
        return WorkflowNodeQueryResponseDTO().apply {

        }
    }

    fun toDomainModel4AddRequest(workflow: Workflow, param: WorkflowNodeAddRequestDTO): WorkflowNode {
        return WorkflowNode().apply {
            this.workflow = workflow
        }
    }

    fun toDomainModel4EditRequest(workflowNode: WorkflowNode, param: WorkflowNodeEditRequestDTO): WorkflowNode {
        TODO("Not yet implemented")
    }
}