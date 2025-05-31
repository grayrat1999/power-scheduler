package org.grayrat.powerscheduler.common.enums

import org.grayrat.powerscheduler.common.annotation.Metadata

/**
 * @author grayrat
 * @since 2025/5/18
 * @description TODO
 */
@Metadata(label = "任务类型", code = "JobTypeEnum")
enum class JobTypeEnum(
    override val label: String
) : BaseEnum {
    /**
     * Java任务
     */
    JAVA("Java任务"),

    /**
     * 脚本
     */
    SCRIPT("脚本任务"),
    ;

    override val code = this.name
}