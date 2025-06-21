package tech.powerscheduler.server.application.dto.request

import jakarta.validation.constraints.NotBlank
import tech.powerscheduler.common.dto.request.PageQueryRequestDTO

/**
 * 应用分组查询请求参数
 *
 * @author grayrat
 * @since 2025/4/16
 */
class AppGroupQueryRequestDTO : PageQueryRequestDTO() {
    /**
     * 命名空间编码
     */
    @NotBlank
    var namespaceCode: String? = null

    /**
     * 应用分组编码
     */
    var code: String? = null

    /**
     * 应用分组名称
     */
    var name: String? = null
}