package tech.powerscheduler.common.annotation

/**
 * 元数据注解
 * @author grayrat
 * @since 2025/5/18
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Metadata(
    /**
     * 标签
     */
    val label: String,
    /**
     * 编码
     */
    val code: String
)
