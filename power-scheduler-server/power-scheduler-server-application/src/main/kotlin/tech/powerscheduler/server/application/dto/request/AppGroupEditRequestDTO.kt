package tech.powerscheduler.server.application.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/**
 * 应用分组编辑请求参数
 *
 * @author grayrat
 * @since 2025/4/19
 */
class AppGroupEditRequestDTO {

    /**
     * 应用id
     */
    @NotNull
    var id: Long? = null

    /**
     * 应用分组名称
     */
    @NotBlank
    var name: String? = null
}