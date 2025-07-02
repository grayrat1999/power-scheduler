package tech.powerscheduler.server.application.dto.request

import jakarta.validation.constraints.NotEmpty
import tech.powerscheduler.common.enums.ExecuteModeEnum
import tech.powerscheduler.common.enums.JobTypeEnum
import tech.powerscheduler.common.enums.ScriptTypeEnum

/**
 * @author grayrat
 * @since 2025/6/23
 */
class WorkflowNodeSaveDagRequestDTO {

    /**
     * 工作流ID
     */
    var workflowId: Long? = null

    /**
     * 邮箱无环图
     */
    @NotEmpty
    var dagNodes: List<Node> = emptyList()

    val flattenedNodes: List<Node>
        get() = dagNodes.flatMap { it.flatten() }

    class Node {
        /**
         * 工作流节点ID
         */
        var workflowNodeId: Long? = null

        /**
         * UUID
         */
        var uuid: String? = null

        /**
         * 子节点列表
         */
        var children: List<Node>? = null

        /**
         * 任务名称
         */
        var jobName: String? = null

        /**
         * 任务描述
         */
        var jobDesc: String? = null

        /**
         * 任务类型
         */
        var jobType: JobTypeEnum? = null

        /**
         * 任务处理器
         */
        var processor: String? = null

        /**
         * 执行模式
         */
        var executeMode: ExecuteModeEnum? = null

        /**
         * 任务参数
         */
        var executeParams: String? = null

        /**
         * 脚本类型
         */
        var scriptType: ScriptTypeEnum? = null

        /**
         * 脚本源代码
         */
        var scriptCode: String? = null

        /**
         * 最大重试次数
         */
        var maxAttemptCnt: Int? = null

        /**
         * 重试间隔(s)
         */
        var attemptInterval: Int? = null

        /**
         * 子任务最大重试次数
         */
        var taskMaxAttemptCnt: Int? = null

        /**
         * 子任务重试间隔(s)
         */
        var taskAttemptInterval: Int? = null

        /**
         * 优先级
         */
        var priority: Int? = null

        fun flatten(): Set<Node> {
            return setOf(this) + children.orEmpty().flatMap { it.flatten() }
        }
    }

    private enum class VisitState { UNVISITED, VISITING, VISITED }

    fun isDag(): Boolean {
        val stateMap = mutableMapOf<Node, VisitState>()

        fun hasCycle(node: Node): Boolean {
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