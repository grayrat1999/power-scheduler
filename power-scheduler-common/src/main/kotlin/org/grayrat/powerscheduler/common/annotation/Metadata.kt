package org.grayrat.powerscheduler.common.annotation

/**
 * @author grayrat
 * @since 2025/5/18
 * @description TODO
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Metadata(
    val label: String,
    val code: String
)
