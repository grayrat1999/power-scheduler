package tech.powerscheduler.server.domain.workflow

/**
 * @author grayrat
 * @since 2025/7/3
 */
class WorkflowGraph(
    /**
     * 有向无环图
     */
    var nodes: List<Node> = emptyList()
) {

    inner class Node {
        /**
         * UUID
         */
        var uuid: String? = null

        /**
         * 父节点UUID
         */
        var parentUuid: String? = null

        /**
         * 子节点列表
         */
        val children: List<Node>
            get() = nodes.filter { it.parentUuid == this.uuid }
    }

    private enum class VisitState { UNVISITED, VISITING, VISITED }

    fun isDag(): Boolean {
        val stateMap = mutableMapOf<Node, VisitState>()

        fun hasCycle(node: Node): Boolean {
            val state = stateMap[node] ?: VisitState.UNVISITED
            if (state == VisitState.VISITING) return true  // 回边，存在环
            if (state == VisitState.VISITED) return false  // 已完成，无需重复判断

            stateMap[node] = VisitState.VISITING
            for (child in node.children) {
                if (hasCycle(child)) return true
            }
            stateMap[node] = VisitState.VISITED
            return false
        }

        return nodes.none { hasCycle(it) }
    }
}