package tech.powerscheduler.server.application.dto.request

import jakarta.validation.constraints.*
import tech.powerscheduler.common.enums.*

/**
 * @author grayrat
 * @since 2025/6/23
 */
class WorkflowAddRequestDTO {
    /**
     * 有向无环图
     */
    @NotEmpty
    var nodes: List<Node> = emptyList()

    /**
     * 命名空间编码
     */
    @NotBlank
    var namespaceCode: String? = null

    /**
     * 应用编码
     */
    @NotBlank
    var appCode: String? = null

    /**
     * 工作流名称
     */
    @NotBlank
    var name: String? = null

    /**
     * 工作流描述
     */
    var description: String? = null

    /**
     * 调度类型
     */
    @NotNull
    var scheduleType: ScheduleTypeEnum? = null

    /**
     * 调度配置
     */
    @NotBlank
    var scheduleConfig: String? = null

    /**
     * 并发数
     */
    @NotNull
    @Positive
    var maxConcurrentNum: Int? = 1

    /**
     * 保留策略
     */
    @NotNull
    var retentionPolicy: RetentionPolicyEnum? = RetentionPolicyEnum.RECENT_COUNT

    /**
     * 保留值
     */
    @NotNull
    @Positive
    var retentionValue: Int? = 100

    /**
     * 图的UI数据
     */
    @NotBlank
    var graphData: String? = null

    inner class Node {
        /**
         * UUID
         */
        @NotBlank
        var uuid: String? = null

        /**
         * 父节点UUID
         */
        var parentUuid: String? = null

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
        @PositiveOrZero
        var priority: Int? = 1
    }
}