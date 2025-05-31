package org.grayrat.powerscheduler.server.application.utils

import org.grayrat.powerscheduler.server.application.dto.response.PageDTO
import org.grayrat.powerscheduler.server.domain.common.Page

/**
 * @author grayrat
 * @since 2025/4/18
 */
fun <T> Page<T>.toDTO(): PageDTO<T> {
    return PageDTO(
        number = this.number,
        size = this.size,
        totalElements = this.totalElements,
        totalPages = this.totalPages,
        content = this.content
    )
}
