package tech.powerscheduler.server.domain.workflow

import tech.powerscheduler.server.domain.common.Page

/**
 * @author grayrat
 * @since 2025/6/22
 */
interface WorkflowRepository {

    fun findById(workflowId: WorkflowId): Workflow?

    fun pageQuery(query: WorkflowQuery): Page<Workflow>

    fun save(workflow: Workflow): WorkflowId

    fun deleteById(workflowId: WorkflowId)

}