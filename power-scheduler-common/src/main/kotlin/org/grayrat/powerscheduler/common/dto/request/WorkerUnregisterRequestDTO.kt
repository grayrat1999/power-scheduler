package org.grayrat.powerscheduler.common.dto.request

import jakarta.validation.constraints.NotBlank

/**
 * worker下线请求参数
 *
 * @author grayrat
 * @since 2025/5/20
 */
class WorkerUnregisterRequestDTO {
    @NotBlank
    var appCode: String? = null

    var host: String? = null

    @NotBlank
    var port: Int? = null

    @NotBlank
    var accessToken: String? = null
}
