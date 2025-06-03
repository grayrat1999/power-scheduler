package tech.powerscheduler.common.enums

import tech.powerscheduler.common.annotation.Metadata

/**
 * 执行模式枚举
 *
 * @author grayrat
 * @since 2025/5/18
 */
@Metadata(label = "执行模式", code = "ExecuteModeEnum")
enum class ExecuteModeEnum(
    override val label: String
) : BaseEnum {
    /**
     * 单机模式
     */
    SINGLE("单机模式"),

    /**
     * 广播模式
     */
    BROADCAST("广播模式"),

    /**
     * MapReduce模式
     */
    MAP_REDUCE("MapReduce模式"),
    ;

    override val code = this.name
}