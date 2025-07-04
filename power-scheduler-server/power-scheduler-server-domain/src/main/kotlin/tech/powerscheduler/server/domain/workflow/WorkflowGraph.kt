package tech.powerscheduler.server.domain.workflow

/**
 * @author grayrat
 * @since 2025/7/3
 */
class WorkflowGraph(
    /**
     * 有向无环图
     */
    val nodes: List<WorkflowGraphNode> = emptyList()
) {

    private enum class VisitState { UNVISITED, VISITING, VISITED }

    fun isDag(): Boolean {
        val stateMap = mutableMapOf<WorkflowGraphNode, VisitState>()
        val uuid2node = nodes.associateBy { it.uuid }

        fun hasCycle(node: WorkflowGraphNode): Boolean {
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
}