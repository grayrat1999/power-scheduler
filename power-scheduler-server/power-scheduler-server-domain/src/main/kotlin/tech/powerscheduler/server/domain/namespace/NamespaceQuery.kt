package tech.powerscheduler.server.domain.namespace

import tech.powerscheduler.server.domain.common.PageQuery

/**
 * @author grayrat
 * @since 2025/6/21
 */
class NamespaceQuery : PageQuery() {
    /**
     * 命名空间名称
     */
    var name: String? = null
}