package tech.powerscheduler.server.domain.workflow

open class WorkflowGraphNode {
    /**
     * UUID
     */
    open var uuid: String? = null

    /**
     * 子节点UUID
     */
    open var childrenUuids: Set<String> = emptySet()
}