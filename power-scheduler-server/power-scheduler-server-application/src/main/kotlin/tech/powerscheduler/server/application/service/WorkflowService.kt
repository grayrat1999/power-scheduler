package tech.powerscheduler.server.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import tech.powerscheduler.common.dto.response.PageDTO
import tech.powerscheduler.common.exception.BizException
import tech.powerscheduler.server.application.assembler.WorkflowAssembler
import tech.powerscheduler.server.application.assembler.WorkflowNodeAssembler
import tech.powerscheduler.server.application.dto.request.WorkflowAddRequestDTO
import tech.powerscheduler.server.application.dto.request.WorkflowEditRequestDTO
import tech.powerscheduler.server.application.dto.request.WorkflowQueryRequestDTO
import tech.powerscheduler.server.application.dto.request.WorkflowSwitchRequestDTO
import tech.powerscheduler.server.application.dto.response.WorkflowQueryResponseDTO
import tech.powerscheduler.server.application.utils.toDTO
import tech.powerscheduler.server.domain.appgroup.AppGroupRepository
import tech.powerscheduler.server.domain.namespace.NamespaceRepository
import tech.powerscheduler.server.domain.workflow.*

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

    fun list(param: WorkflowQueryRequestDTO): PageDTO<WorkflowQueryResponseDTO> {
        val query = workflowAssembler.toDomainQuery(param)
        val page = workflowRepository.pageQuery(query)
        return page.toDTO().map { workflowAssembler.toWorkflowQueryResponseDTO(it) }
    }

    @Transactional
    fun add(param: WorkflowAddRequestDTO): Long {
        validateDag(param.nodes)
        val namespace = namespaceRepository.findByCode(param.namespaceCode!!)
            ?: throw BizException("namespace not found")
        val appGroup = appGroupRepository.findByCode(namespace, param.appCode!!)
            ?: throw BizException("appGroup not found")
        val workflow = workflowAssembler.toDomainModel4AddRequest(appGroup = appGroup, param = param)
        val workflowNodes = workflowNodeAssembler.toDomainModel4AddRequest(
            workflow = workflow,
            nodes = param.nodes,
        )
        workflow.workflowNodes = workflowNodes
        val workflowId = workflowRepository.save(workflow)
//        workflowNodeRepository.saveAll(workflowNodes)
        return workflowId.value
    }

    fun edit(param: WorkflowEditRequestDTO) {
        validateDag(param.nodes)
        val workflowId = WorkflowId(param.workflowId!!)
        val workflow = workflowRepository.findById(workflowId)
            ?: throw BizException("Workflow not found")
        val workflowToSave = workflowAssembler.toDomainModel4EditRequest(workflow = workflow, param = param)
        workflowRepository.save(workflowToSave)
    }

    fun validateDag(nodes: List<WorkflowGraphNode>) {
        if (nodes.size < 2) {
            throw BizException("The number of nodes should be at least 2.")
        }
        if (WorkflowGraph(nodes).isDag().not()) {
            throw BizException("not a valid dag")
        }
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