package tech.powerscheduler.server.application.dto.request

import jakarta.validation.constraints.NotBlank

/**
 * @author grayrat
 * @since 2025/6/21
 */
class NamespaceAddRequestDTO {
    /**
     * 命名空间编码
     */
    @NotBlank
    var code: String? = null

    /**
     * 命名空间名称
     */
    @NotBlank
    var name: String? = null

    /**
     * 命名空间描述
     */
    var description: String? = null
}