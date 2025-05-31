package org.grayrat.powerscheduler.common.enums

import org.grayrat.powerscheduler.common.annotation.Metadata

/**
 * @author grayrat
 * @since 2025/5/18
 * @description TODO
 */
@Metadata(label = "调度方式", code = "ScheduleTypeEnum")
enum class ScheduleTypeEnum(
    override val label: String
) : BaseEnum {
    /**
     * CRON
     */
    CRON("CRON"),

    /**
     * 固定频率
     */
    FIX_RATE("固定频率"),

    /**
     * 固定延迟
     */
    FIX_DELAY("固定延迟"),

    /**
     * 一次性任务
     */
    ONE_TIME("一次性任务"),
    ;

    override val code = this.name
}