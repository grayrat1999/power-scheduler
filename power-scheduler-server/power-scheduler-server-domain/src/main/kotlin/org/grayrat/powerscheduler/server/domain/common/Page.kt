package org.grayrat.powerscheduler.server.domain.common

/**
 * 分页容器
 *
 * @author grayrat
 * @since 2025/4/17
 */
class Page<T>(
    var number: Int = 1,
    var size: Int = 20,
    var totalPages: Int = 1,
    var totalElements: Long = 0L,
    var content: List<T> = emptyList()
) {
    fun isNotEmpty(): Boolean = content.isNotEmpty()
}
