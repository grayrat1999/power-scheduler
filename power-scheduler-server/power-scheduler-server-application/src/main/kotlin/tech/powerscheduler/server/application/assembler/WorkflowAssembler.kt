package tech.powerscheduler.server.application.assembler

import org.springframework.stereotype.Component
import tech.powerscheduler.server.application.dto.request.WorkflowAddRequestDTO
import tech.powerscheduler.server.application.dto.request.WorkflowEditRequestDTO
import tech.powerscheduler.server.domain.workflow.Workflow

/**
 * @author grayrat
 * @since 2025/6/23
 */
@Component
class WorkflowAssembler {
    fun toDomainModel4AddRequest(param: WorkflowAddRequestDTO): Workflow {
        return Workflow().apply {

        }
    }

    fun toDomainModel4EditRequest(workflow: Workflow, param: WorkflowEditRequestDTO): Workflow {
        return Workflow().apply {

        }
    }

}