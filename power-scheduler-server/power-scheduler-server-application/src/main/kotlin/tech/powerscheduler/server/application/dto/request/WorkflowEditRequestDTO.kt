package tech.powerscheduler.server.application.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import org.jetbrains.annotations.NotNull
import tech.powerscheduler.common.enums.RetentionPolicyEnum
import tech.powerscheduler.common.enums.ScheduleTypeEnum

/**
 * @author grayrat
 * @since 2025/6/23
 */
class WorkflowEditRequestDTO {
    /**
     * 工作流id
     */
    @NotNull
    var workflowId: Long? = null

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
}