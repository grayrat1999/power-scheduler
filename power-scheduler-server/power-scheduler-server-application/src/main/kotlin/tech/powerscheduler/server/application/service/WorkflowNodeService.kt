package tech.powerscheduler.server.application.service

import org.springframework.stereotype.Service
import tech.powerscheduler.common.exception.BizException
import tech.powerscheduler.server.application.assembler.WorkflowNodeAssembler
import tech.powerscheduler.server.application.dto.request.WorkflowNodeAddRequestDTO
import tech.powerscheduler.server.application.dto.request.WorkflowNodeEditRequestDTO
import tech.powerscheduler.server.application.dto.request.WorkflowNodeSaveDagRequestDTO
import tech.powerscheduler.server.application.dto.response.WorkflowNodeQueryResponseDTO
import tech.powerscheduler.server.domain.workflow.WorkflowId
import tech.powerscheduler.server.domain.workflow.WorkflowNodeId
import tech.powerscheduler.server.domain.workflow.WorkflowNodeRepository
import tech.powerscheduler.server.domain.workflow.WorkflowRepository

/**
 * @author grayrat
 * @since 2025/6/23
 */
@Service
class WorkflowNodeService(
    private val workflowNodeAssembler: WorkflowNodeAssembler,
    private val workflowRepository: WorkflowRepository,
    private val workflowNodeRepository: WorkflowNodeRepository,
) {

    fun list(workflowId: Long): List<WorkflowNodeQueryResponseDTO> {
        val workflow = workflowRepository.findById(WorkflowId(workflowId))
        if (workflow == null) {
            return emptyList()
        }
        val workflowNodes = workflowNodeRepository.findAllByWorkflow(workflow)
        return workflowNodes.map { workflowNodeAssembler.toWorkflowNodeQueryResponseDTO(it) }
    }

    fun add(param: WorkflowNodeAddRequestDTO): Long {
        val workflowId = WorkflowId(param.workflowId!!)
        val workflow = workflowRepository.findById(workflowId)
            ?: throw BizException("Workflow not found")
        val workflowNode = workflowNodeAssembler.toDomainModel4AddRequest(workflow, param)
        val workflowNodeId = workflowNodeRepository.save(workflowNode)
        return workflowNodeId.value
    }

    fun edit(param: WorkflowNodeEditRequestDTO) {
        val workflowNodeId = WorkflowNodeId(param.workflowNodeId!!)
        val workflowNode = workflowNodeRepository.findById(workflowNodeId)
            ?: throw BizException("workflowNode not found")
        val workflowNodeToSave = workflowNodeAssembler.toDomainModel4EditRequest(workflowNode, param)
        workflowNodeRepository.save(workflowNodeToSave)
    }

    fun saveDag(param: WorkflowNodeSaveDagRequestDTO) {
        if (param.isDag().not()) {
            throw BizException("exist circle in graph")
        }
        val workflowNodeId2ChildrenIds = param.flattenedNodes.associate {
            Pair(
                WorkflowNodeId(it.workflowNodeId!!),
                it.children.orEmpty().map { child -> WorkflowNodeId(child.workflowNodeId!!) }
            )
        }
        val allIds = workflowNodeId2ChildrenIds.keys
        val workflowNodes = workflowNodeRepository.findAllByIds(allIds)
        val id2WorkflowNode = workflowNodes.associateBy { it.id!! }
        for (workflowNode in workflowNodes) {
            val childrenIds = workflowNodeId2ChildrenIds[workflowNode.id].orEmpty()
            workflowNode.children = childrenIds.mapNotNull { id2WorkflowNode[it] }.toSet()
        }
        workflowNodeRepository.saveAll(workflowNodes)
    }

    fun delete(id: Long) {
        val workflowNodeId = WorkflowNodeId(id)
        workflowNodeRepository.deleteById(workflowNodeId)
    }

}