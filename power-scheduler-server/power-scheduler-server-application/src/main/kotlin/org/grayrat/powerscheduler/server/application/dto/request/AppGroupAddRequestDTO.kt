package org.grayrat.powerscheduler.server.application.dto.request

import jakarta.validation.constraints.NotBlank

/**
 * 应用分组新增请求参数
 *
 * @author grayrat
 * @since 2025/4/16
 */
class AppGroupAddRequestDTO {
    /**
     * 应用分组编码
     */
    @NotBlank
    var code: String? = null

    /**
     * 应用分组名称
     */
    @NotBlank
    var name: String? = null
}