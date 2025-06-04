package tech.powerscheduler.common.enums

import tech.powerscheduler.common.annotation.Metadata

/**
 * 任务实例保留策略
 *
 * @author grayrat
 * @since 2025/6/4
 */
@Metadata(label = "保留策略", code = "RetentionPolicyEnum")
enum class RetentionPolicyEnum(
    override val label: String
) : BaseEnum {

    /**
     * 只保留最近N天内的任务实例
     */
    RECENT_DAYS("最近N天"),

    /**
     * 只保留最近N条的任务实例
     */
    RECENT_COUNT("最近N条"),
    ;

    override val code = this.name
}