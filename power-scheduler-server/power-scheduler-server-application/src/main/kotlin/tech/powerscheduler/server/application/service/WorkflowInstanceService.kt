package tech.powerscheduler.server.application.service

import org.springframework.stereotype.Service
import tech.powerscheduler.common.dto.response.PageDTO
import tech.powerscheduler.server.application.assembler.WorkflowInstanceAssembler
import tech.powerscheduler.server.application.dto.request.WorkflowInstanceQueryRequestDTO
import tech.powerscheduler.server.application.dto.response.WorkflowInstanceDetailResponseDTO
import tech.powerscheduler.server.application.dto.response.WorkflowInstanceQueryResponseDTO
import tech.powerscheduler.server.application.utils.toDTO
import tech.powerscheduler.server.domain.workflow.WorkflowInstanceId
import tech.powerscheduler.server.domain.workflow.WorkflowInstanceRepository

/**
 * @author grayrat
 * @since 2025/7/9
 */
@Service
class WorkflowInstanceService(
    private val workflowInstanceRepository: WorkflowInstanceRepository,
    private val workflowInstanceAssembler: WorkflowInstanceAssembler,
) {

    fun list(param: WorkflowInstanceQueryRequestDTO): PageDTO<WorkflowInstanceQueryResponseDTO> {
        val query = workflowInstanceAssembler.toDomainQuery(param)
        val page = workflowInstanceRepository.pageQuery(query)
        return page.toDTO().map { workflowInstanceAssembler.toWorkflowInstanceQueryResponseDTO(it) }
    }

    fun get(workflowInstanceId: Long): WorkflowInstanceDetailResponseDTO? {
        val workflowInstance = workflowInstanceRepository.findById(WorkflowInstanceId(workflowInstanceId))
        return workflowInstance?.let { workflowInstanceAssembler.toWorkflowInstanceDetailResponseDTO(it) }
    }

}