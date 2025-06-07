package tech.powerscheduler.common.enums

import tech.powerscheduler.common.annotation.Metadata

/**
 * @author grayrat
 * @since 2025/6/7
 */
@Metadata(label = "子任务状态", code = "TaskStatusEnum")
enum class TaskStatusEnum (
    override val label: String
) : BaseEnum {
    /**
     * 待调度
     */
    WAITING_DISPATCH("待调度"),

    /**
     * 调度中
     */
    DISPATCHING("调度中"),

    /**
     * 排队中
     */
    PENDING("排队中"),

    /**
     * 执行中
     */
    PROCESSING("执行中"),

    /**
     * 失败
     */
    FAILED("失败"),

    /**
     * 成功
     */
    SUCCESS("成功"),

    /**
     * 取消
     */
    CANCELED("取消")
    ;

    override val code = this.name

    companion object {
        /**
         * 未完成状态集合
         */
        val UNCOMPLETED_STATUSES = setOf(
            WAITING_DISPATCH,
            PENDING,
            PROCESSING,
        )

        /**
         * 已完成状态集合
         */
        val COMPLETED_STATUSES = setOf(
            SUCCESS,
            FAILED,
            CANCELED,
        )
    }
}