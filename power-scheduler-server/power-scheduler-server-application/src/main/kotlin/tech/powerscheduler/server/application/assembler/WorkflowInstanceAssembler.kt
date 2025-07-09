package tech.powerscheduler.server.application.assembler

import org.springframework.stereotype.Component
import tech.powerscheduler.server.application.dto.request.WorkflowInstanceQueryRequestDTO
import tech.powerscheduler.server.application.dto.response.WorkflowInstanceDetailResponseDTO
import tech.powerscheduler.server.application.dto.response.WorkflowInstanceQueryResponseDTO
import tech.powerscheduler.server.domain.workflow.WorkflowInstance
import tech.powerscheduler.server.domain.workflow.WorkflowInstanceQuery

/**
 * @author grayrat
 * @since 2025/7/9
 */
@Component
class WorkflowInstanceAssembler {

    fun toDomainQuery(param: WorkflowInstanceQueryRequestDTO): WorkflowInstanceQuery {
        return WorkflowInstanceQuery().apply {

        }
    }

    fun toWorkflowInstanceQueryResponseDTO(workflowInstance: WorkflowInstance): WorkflowInstanceQueryResponseDTO {
        return WorkflowInstanceQueryResponseDTO().apply {

        }
    }

    fun toWorkflowInstanceDetailResponseDTO(workflowInstance: WorkflowInstance): WorkflowInstanceDetailResponseDTO {
        return WorkflowInstanceDetailResponseDTO().apply {

        }
    }

}