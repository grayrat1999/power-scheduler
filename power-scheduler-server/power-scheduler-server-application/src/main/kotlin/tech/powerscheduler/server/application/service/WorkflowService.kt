package tech.powerscheduler.server.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import tech.powerscheduler.common.dto.response.PageDTO
import tech.powerscheduler.common.exception.BizException
import tech.powerscheduler.server.application.assembler.WorkflowAssembler
import tech.powerscheduler.server.application.assembler.WorkflowNodeAssembler
import tech.powerscheduler.server.application.dto.request.*
import tech.powerscheduler.server.application.dto.response.WorkflowDetailResponseDTO
import tech.powerscheduler.server.application.dto.response.WorkflowQueryResponseDTO
import tech.powerscheduler.server.application.utils.toDTO
import tech.powerscheduler.server.domain.appgroup.AppGroupRepository
import tech.powerscheduler.server.domain.namespace.NamespaceRepository
import tech.powerscheduler.server.domain.workflow.WorkflowId
import tech.powerscheduler.server.domain.workflow.WorkflowNodeRepository
import tech.powerscheduler.server.domain.workflow.WorkflowRepository

/**
 * @author grayrat
 * @since 2025/6/23
 */
@Service
class WorkflowService(
    private val namespaceRepository: NamespaceRepository,
    private val appGroupRepository: AppGroupRepository,
    private val workflowAssembler: WorkflowAssembler,
    private val workflowNodeAssembler: WorkflowNodeAssembler,
    private val workflowRepository: WorkflowRepository,
    private val workflowNodeRepository: WorkflowNodeRepository,
    private val transactionTemplate: TransactionTemplate,
) {

    @Transactional
    fun list(param: WorkflowQueryRequestDTO): PageDTO<WorkflowQueryResponseDTO> {
        val query = workflowAssembler.toDomainQuery(param)
        val page = workflowRepository.pageQuery(query)
        return page.toDTO().map { workflowAssembler.toWorkflowQueryResponseDTO(it) }
    }

    @Transactional
    fun get(workflowId: Long): WorkflowDetailResponseDTO? {
        val workflow = workflowRepository.findById(WorkflowId(workflowId))
        return workflow?.let { workflowAssembler.toWorkflowDetailResponseDTO(it) }
    }

    @Transactional
    fun add(param: WorkflowAddRequestDTO): Long {
        validateDag(param.nodes)
        val namespace = namespaceRepository.findByCode(param.namespaceCode!!)
            ?: throw BizException("namespace not found")
        val appGroup = appGroupRepository.findByCode(namespace, param.appCode!!)
            ?: throw BizException("appGroup not found")
        val workflowToSave = workflowAssembler.toDomainModel4AddRequest(appGroup = appGroup, param = param)
        val workflowNodesToSave = workflowNodeAssembler.toDomainModel4AddRequest(
            workflow = workflowToSave,
            nodes = param.nodes,
        )
        workflowToSave.workflowNodes = workflowNodesToSave
        val workflowId = workflowRepository.save(workflowToSave)
        return workflowId.value
    }

    @Transactional
    fun edit(param: WorkflowEditRequestDTO) {
        validateDag(param.nodes)
        val workflowId = WorkflowId(param.workflowId!!)
        val workflow = workflowRepository.findById(workflowId)
            ?: throw BizException("Workflow not found")
        val workflowToSave = workflowAssembler.toDomainModel4EditRequest(workflow = workflow, param = param)
        val workflowNodesToSave = workflowNodeAssembler.toDomainModel4EditRequest(
            workflow = workflow,
            nodes = param.nodes,
            existNodes = workflow.workflowNodes,
        )
        workflowToSave.workflowNodes = workflowNodesToSave
        workflowRepository.save(workflowToSave)
    }

    private enum class VisitState { UNVISITED, VISITING, VISITED }

    fun validateDag(nodes: List<WorkflowNodeDTO>) {
        if (nodes.size < 2) {
            throw BizException("The number of nodes should be at least 2.")
        }
        if (isDag(nodes).not()) {
            throw BizException("not a valid dag")
        }
    }

    fun isDag(nodes: List<WorkflowNodeDTO>): Boolean {
        val stateMap = mutableMapOf<WorkflowNodeDTO, VisitState>()
        val uuid2node = nodes.associateBy { it.uuid }

        fun hasCycle(node: WorkflowNodeDTO): Boolean {
            val state = stateMap[node] ?: VisitState.UNVISITED
            if (state == VisitState.VISITING) return true  // 回边，存在环
            if (state == VisitState.VISITED) return false  // 已完成，无需重复判断

            stateMap[node] = VisitState.VISITING
            val children = node.childrenUuids.mapNotNull { uuid2node[it] }
            for (child in children) {
                if (hasCycle(child)) return true
            }
            stateMap[node] = VisitState.VISITED
            return false
        }

        return nodes.none { hasCycle(it) }
    }

    fun switch(param: WorkflowSwitchRequestDTO) {
        val workflowId = WorkflowId(param.workflowId!!)
        val workflow = workflowRepository.findById(workflowId)
            ?: throw BizException("Workflow not found")
        if (workflow.enabled == param.enabled) {
            return
        }
        workflow.apply {
            this.enabled = param.enabled
            if (this.enabled == true) {
                this.initNextScheduleTime()
            } else {
                this.nextScheduleAt = null
            }
        }
        workflowRepository.save(workflow)
    }

    fun delete(id: Long) {
        val workflowId = WorkflowId(id)
        val workflow = workflowRepository.findById(workflowId)
        if (workflow == null) {
            return
        }
        val workflowNodes = workflowNodeRepository.findAllByWorkflow(workflow)
        val workflowNodeIds = workflowNodes.mapNotNull { it.id }
        transactionTemplate.executeWithoutResult {
            workflowRepository.deleteById(workflow.id!!)
            workflowNodeRepository.deleteByIds(workflowNodeIds)
        }
    }

}