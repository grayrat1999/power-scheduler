package tech.powerscheduler.server.domain.workflow

import tech.powerscheduler.common.enums.RetentionPolicyEnum
import tech.powerscheduler.common.enums.WorkflowStatusEnum
import tech.powerscheduler.server.domain.appgroup.AppGroup
import tech.powerscheduler.server.domain.common.Schedulable
import java.time.LocalDateTime

/**
 * @author grayrat
 * @since 2025/6/21
 */
class Workflow : Schedulable() {

    /**
     * 应用分组
     */
    var appGroup: AppGroup? = null

    /**
     * 工作流节点
     */
    var workflowNodes: List<WorkflowNode> = emptyList()

    /**
     * 主键
     */
    var id: WorkflowId? = null

    /**
     * 工作流名称
     */
    var name: String? = null

    /**
     * 工作流描述
     */
    var description: String? = null

    /**
     * 启用状态
     */
    var enabled: Boolean? = null

    /**
     * 并发数
     */
    var maxConcurrentNum: Int? = null

    /**
     * 保留策略
     */
    var retentionPolicy: RetentionPolicyEnum? = null

    /**
     * 保留值
     */
    var retentionValue: Int? = null

    /**
     * 有向无环图的UI数据
     */
    var graphData: String? = null

    /**
     * 创建人
     */
    var createdBy: String? = null

    /**
     * 创建时间
     */
    var createdAt: LocalDateTime? = null

    /**
     * 修改人
     */
    var updatedBy: String? = null

    /**
     * 修改时间
     */
    var updatedAt: LocalDateTime? = null

    fun createInstance(): WorkflowInstance {
        return WorkflowInstance().also {
            it.appGroup = this.appGroup
            it.name = this.name
            it.status = WorkflowStatusEnum.RUNNING
        }
    }
}