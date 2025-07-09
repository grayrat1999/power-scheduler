package tech.powerscheduler.server.domain.workflow

import tech.powerscheduler.common.enums.ExecuteModeEnum
import tech.powerscheduler.common.enums.JobTypeEnum
import tech.powerscheduler.common.enums.ScriptTypeEnum
import tech.powerscheduler.common.enums.WorkflowStatusEnum
import tech.powerscheduler.server.domain.job.JobInstance
import java.time.LocalDateTime

/**
 * @author grayrat
 * @since 2025/6/21
 */
class WorkflowNodeInstance {

    /**
     * 父节点
     */
    var parents: Set<WorkflowNodeInstance> = emptySet()

    /**
     * 子节点
     */
    var children: Set<WorkflowNodeInstance> = emptySet()

    /**
     * 主键
     */
    var id: WorkflowNodeInstanceId? = null

    /**
     * 节点编码
     */
    var nodeCode: String? = null

    /**
     * 节点实例编号
     */
    var nodeInstanceCode: String? = null

    /**
     * 节点名称
     */
    var name: String? = null

    /**
     * 任务类型
     */
    var jobType: JobTypeEnum? = null

    /**
     * 任务处理器
     */
    var processor: String? = null

    /**
     * 任务状态
     */
    var status: WorkflowStatusEnum? = null

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
     * 数据时间
     */
    var dataTime: LocalDateTime? = null

    /**
     * Worker地址（ip:host）
     */
    var workerAddress: String? = null

    /**
     * 当前重试次数
     */
    var attemptCnt: Int? = null

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

    fun createJobInstance(): JobInstance {
        return JobInstance().apply {

        }
    }
}