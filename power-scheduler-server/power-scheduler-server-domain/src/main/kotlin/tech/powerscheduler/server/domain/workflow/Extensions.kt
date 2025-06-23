package tech.powerscheduler.server.domain.workflow

/**
 * @author grayrat
 * @since 2025/6/25
 */
fun Collection<WorkflowNode>.createInstance(): List<WorkflowNodeInstance> {
    val rootNodes = this.asSequence()
        .filter { it.parents.isEmpty() }
        .map {
            it.createInstance().apply {
                this.children = it.children.map(WorkflowNode::createInstance).toSet()
            }
        }
        .toList()
    return rootNodes.flatMap { it.children }
}