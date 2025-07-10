package tech.powerscheduler.server.domain.workflow

/**
 * @author grayrat
 * @since 2025/6/25
 */
fun Collection<WorkflowNode>.createInstance(workflowInstance: WorkflowInstance): List<WorkflowNodeInstance> {
    val rootNodes = this.asSequence()
        .filter { it.parents.isEmpty() }
        .map {
            it.createInstance(workflowInstance).apply {
                this.children = it.children.map { child -> child.createInstance(workflowInstance) }.toSet()
            }
        }
        .toList()
    return rootNodes.flatMap { it.children }
}