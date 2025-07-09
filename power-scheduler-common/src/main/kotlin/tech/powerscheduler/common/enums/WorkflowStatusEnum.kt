package tech.powerscheduler.common.enums

import tech.powerscheduler.common.annotation.Metadata

/**
 * @author grayrat
 * @since 2025/6/25
 */
@Metadata(label = "工作流状态", code = "WorkflowStatusEnum")
enum class WorkflowStatusEnum (
    override val label: String
) : BaseEnum {
    /**
     * 等待
     */
    WAITING("等待"),

    /**
     * 运行中
     */
    RUNNING("运行中"),

    /**
     * 成功
     */
    SUCCEED("成功"),

    /**
     * 失败
     */
    FAILED("失败"),

    /**
     * 取消
     */
    CANCELED("取消"),
    ;

    override val code = this.name
}