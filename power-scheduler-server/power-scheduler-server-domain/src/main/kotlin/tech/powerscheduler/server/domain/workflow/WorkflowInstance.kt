package tech.powerscheduler.server.domain.workflow

import tech.powerscheduler.common.enums.WorkflowStatusEnum
import tech.powerscheduler.server.domain.appgroup.AppGroup
import java.time.LocalDateTime

/**
 * @author grayrat
 * @since 2025/6/21
 */
class WorkflowInstance {
    /**
     * 应用分组
     */
    var appGroup: AppGroup? = null

    /**
     * 工作流
     */
    var workflow: Workflow? = null

    /**
     * 工作流节点实例列表
     */
    var workflowNodeInstances: List<WorkflowNodeInstance> = emptyList()

    /**
     * 主键
     */
    var id: WorkflowId? = null

    /**
     * 工作流名称
     */
    var name: String? = null

    /**
     * 状态
     */
    var status: WorkflowStatusEnum? = null

    /**
     * 数据时间
     */
    var dataTime: LocalDateTime? = null

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
}