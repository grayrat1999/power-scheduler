package tech.powerscheduler.server.application.dto.request

import tech.powerscheduler.server.domain.common.PageQuery

/**
 * @author grayrat
 * @since 2025/4/16
 */
open class PageQueryRequestDTO(
    /**
     * 当前页号
     */
    var pageNo: Int = 1,
    /**
     * 每页条数
     */
    var pageSize: Int = 10,
) {
    fun toDomainQuery(): PageQuery {
        return PageQuery().also {
            it.pageNo = pageNo
            it.pageSize = pageSize
        }
    }
}