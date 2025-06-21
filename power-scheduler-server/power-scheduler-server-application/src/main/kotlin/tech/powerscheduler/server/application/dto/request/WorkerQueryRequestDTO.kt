package tech.powerscheduler.server.application.dto.request

import jakarta.validation.constraints.NotBlank

/**
 * @author grayrat
 * @since 2025/6/21
 */
class WorkerQueryRequestDTO {
    @NotBlank
    var namespaceCode: String? = null

    @NotBlank
    var appCode: String? = null
}