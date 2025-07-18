package tech.powerscheduler.server.domain.appgroup

import tech.powerscheduler.server.domain.common.PageQuery

/**
 * 应用分组查询
 *
 * @author grayrat
 * @since 2025/4/17
 */
class AppGroupQuery : PageQuery() {
    /**
     * 命名空间编码
     */
    var namespaceCode: String? = null

    /**
     * 应用编码
     */
    var code: String? = null

    /**
     * 应用分组名称
     */
    var name: String? = null
}