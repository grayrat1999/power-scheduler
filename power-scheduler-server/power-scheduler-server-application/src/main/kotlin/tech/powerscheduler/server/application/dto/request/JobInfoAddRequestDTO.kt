package tech.powerscheduler.server.application.dto.request


import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import tech.powerscheduler.common.enums.ExecuteModeEnum
import tech.powerscheduler.common.enums.JobTypeEnum
import tech.powerscheduler.common.enums.ScheduleTypeEnum
import tech.powerscheduler.common.enums.ScriptTypeEnum

/**
 * 任务新增请求参数
 *
 * @author grayrat
 * @since 2025/4/16
 */
class JobInfoAddRequestDTO {
    /**
     * 应用分组
     */
    @NotBlank
    var appCode: String? = null

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
     * 任务类型
     */
    @NotNull
    var jobType: JobTypeEnum? = null

    /**
     * 任务处理器
     */
    var processor: String? = null

    /**
     * 执行模式
     */
    @NotNull
    var executeMode: ExecuteModeEnum? = ExecuteModeEnum.SINGLE

    /**
     * 执行参数
     */
    var executeParams: String? = null

    /**
     * 最大实例并发数
     */
    @Positive
    var maxConcurrentNum: Int = 1

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
    @PositiveOrZero
    var maxAttemptCnt: Int = 0

    /**
     * 优先级
     */
    @PositiveOrZero
    var priority: Int = 1

    /**
     * 重试间隔(s)
     */
    @PositiveOrZero
    var attemptInterval: Int? = null
}