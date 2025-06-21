package tech.powerscheduler.server.application.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/**
 * @author grayrat
 * @since 2025/6/21
 */
class NamespaceEditRequestDTO {
    /**
     * 命名空间id
     */
    @NotNull
    var id: Long? = null

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