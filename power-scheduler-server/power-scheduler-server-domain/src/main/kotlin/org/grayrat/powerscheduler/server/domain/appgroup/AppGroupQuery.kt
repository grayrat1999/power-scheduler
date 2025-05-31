package org.grayrat.powerscheduler.server.domain.appgroup

import org.grayrat.powerscheduler.server.domain.common.PageQuery

/**
 * 应用分组查询
 *
 * @author grayrat
 * @since 2025/4/17
 */
class AppGroupQuery : PageQuery() {
    /**
     * 应用编码
     */
    var code: String? = null

    /**
     * 应用分组名称
     */
    var name: String? = null
}