package tech.powerscheduler.server.application.dto.request

/**
 * 应用分组查询请求参数
 *
 * @author grayrat
 * @since 2025/4/16
 */
class AppGroupQueryRequestDTO : PageQueryRequestDTO() {
    /**
     * 应用分组编码
     */
    var code: String? = null

    /**
     * 应用分组名称
     */
    var name: String? = null
}