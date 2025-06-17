package tech.powerscheduler.common.dto.request

open class PageQueryRequestDTO(
    /**
     * 当前页号
     */
    var pageNo: Int = 1,
    /**
     * 每页条数
     */
    var pageSize: Int = 10,
)