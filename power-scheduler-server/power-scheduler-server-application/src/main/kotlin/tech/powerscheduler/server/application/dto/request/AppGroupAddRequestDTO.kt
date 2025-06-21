package tech.powerscheduler.server.application.dto.request

import jakarta.validation.constraints.NotBlank

/**
 * 应用分组新增请求参数
 *
 * @author grayrat
 * @since 2025/4/16
 */
class AppGroupAddRequestDTO {
    /**
     * 命名空间编码
     */
    @NotBlank
    var namespaceCode: String? = null

    /**
     * 应用编码
     */
    @NotBlank
    var code: String? = null

    /**
     * 应用名称
     */
    @NotBlank
    var name: String? = null
}