package tech.powerscheduler.server.application.dto.request

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

/**
 * @author grayrat
 * @since 2025/6/23
 */
class WorkflowNodeSaveDagRequestDTO {

    /**
     * 邮箱无环图
     */
    @NotEmpty
    var dagNodes: List<DagNode> = emptyList()

    val flattenedNodes: List<DagNode>
        get() = dagNodes.flatMap { it.flatten() }

    data class DagNode(
        /**
         * 工作流节点ID
         */
        @NotNull
        var workflowNodeId: Long? = null,

        /**
         * 子节点列表
         */
        var children: List<DagNode>? = null,
    ) {
        fun flatten(): Set<DagNode> {
            return setOf(this) + children.orEmpty().flatMap { it.flatten() }
        }
    }

    private enum class VisitState { UNVISITED, VISITING, VISITED }

    fun isDag(): Boolean {
        val stateMap = mutableMapOf<DagNode, VisitState>()

        fun hasCycle(node: DagNode): Boolean {
            val state = stateMap[node] ?: VisitState.UNVISITED
            if (state == VisitState.VISITING) return true  // 回边，存在环
            if (state == VisitState.VISITED) return false  // 已完成，无需重复判断

            stateMap[node] = VisitState.VISITING
            for (child in node.children.orEmpty()) {
                if (hasCycle(child)) return true
            }
            stateMap[node] = VisitState.VISITED
            return false
        }

        return dagNodes.none { hasCycle(it) }
    }
}