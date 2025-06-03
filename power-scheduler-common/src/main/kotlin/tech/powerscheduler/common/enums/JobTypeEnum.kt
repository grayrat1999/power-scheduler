package tech.powerscheduler.common.enums

import tech.powerscheduler.common.annotation.Metadata

/**
 * 任务类型枚举
 *
 * @author grayrat
 * @since 2025/5/18
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