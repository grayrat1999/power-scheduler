package tech.powerscheduler.server.domain.workflow

/**
 * @author grayrat
 * @since 2025/6/22
 */
interface WorkflowRepository {

    fun findById(workflowId: WorkflowId): Workflow?

    fun save(workflow: Workflow): WorkflowId

    fun delete(workflow: Workflow)

}