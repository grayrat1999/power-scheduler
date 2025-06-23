package tech.powerscheduler.server.domain.workflow

/**
 * @author grayrat
 * @since 2025/6/22
 */
interface WorkflowNodeRepository {

    fun findById(workflowNodeId: WorkflowNodeId): WorkflowNode?

    fun findAllByWorkflow(workflow: Workflow): List<WorkflowNode>

    fun save(workflowNode: WorkflowNode): WorkflowNodeId

    fun deleteById(workflowNodeId: WorkflowNodeId)

    fun deleteByIds(ids: Iterable<WorkflowNodeId>)

}