package tech.powerscheduler.server.application.dto.request

import tech.powerscheduler.common.dto.request.PageQueryRequestDTO

/**
 * @author grayrat
 * @since 2025/6/21
 */
class NamespaceQueryRequestDTO : PageQueryRequestDTO() {
    /**
     * 命名空间名称
     */
    var name: String? = null
}