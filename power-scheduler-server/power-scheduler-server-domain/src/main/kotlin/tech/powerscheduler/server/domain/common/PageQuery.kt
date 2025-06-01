package tech.powerscheduler.server.domain.common

/**
 * 分页查询基类
 *
 * @author grayrat
 * @since 2025/4/17
 */
open class PageQuery(
    var pageNo: Int = 1,
    var pageSize: Int = 10,
)