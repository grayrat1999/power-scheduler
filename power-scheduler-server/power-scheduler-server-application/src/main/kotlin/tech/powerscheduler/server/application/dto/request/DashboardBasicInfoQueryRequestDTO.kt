package tech.powerscheduler.server.application.dto.request

import jakarta.validation.constraints.NotBlank

/**
 * @author grayrat
 * @since 2025/6/21
 */
class DashboardBasicInfoQueryRequestDTO {
    /**
     * 命名空间编码
     */
    @NotBlank
    var namespaceCode: String? = null

    /**
     * 应用编码
     */
    var appCode: String? = null
}