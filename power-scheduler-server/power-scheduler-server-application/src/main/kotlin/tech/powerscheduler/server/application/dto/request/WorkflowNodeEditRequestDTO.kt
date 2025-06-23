package tech.powerscheduler.server.application.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import tech.powerscheduler.common.enums.ExecuteModeEnum
import tech.powerscheduler.common.enums.JobTypeEnum
import tech.powerscheduler.common.enums.ScriptTypeEnum

/**
 * @author grayrat
 * @since 2025/6/23
 */
class WorkflowNodeEditRequestDTO {
    /**
     * 工作流节点id
     */
    @NotNull
    var workflowNodeId: Long? = null

    /**
     * 任务名称
     */
    @NotBlank
    var jobName: String? = null

    /**
     * 任务描述
     */
    var jobDesc: String? = null

    /**
     * 任务类型
     */
    @NotNull
    var jobType: JobTypeEnum? = null

    /**
     * 任务处理器
     */
    @NotBlank
    var processor: String? = null

    /**
     * 执行模式
     */
    @NotNull
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
    @NotNull
    @PositiveOrZero
    var maxAttemptCnt: Int? = 0

    /**
     * 重试间隔(s)
     */
    @NotNull
    @PositiveOrZero
    var attemptInterval: Int? = 15

    /**
     * 子任务最大重试次数
     */
    @NotNull
    @PositiveOrZero
    var taskMaxAttemptCnt: Int? = 0

    /**
     * 子任务重试间隔(s)
     */
    @NotNull
    @PositiveOrZero
    var taskAttemptInterval: Int? = 15

    /**
     * 优先级
     */
    @NotNull
    var priority: Int? = 1
}