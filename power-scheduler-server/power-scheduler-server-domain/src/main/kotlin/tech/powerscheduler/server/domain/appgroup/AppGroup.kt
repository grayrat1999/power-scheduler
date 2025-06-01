package tech.powerscheduler.server.domain.appgroup

import java.time.LocalDateTime

/**
 * 应用分组
 *
 * @author grayrat
 * @since 2025/4/16
 */
class AppGroup {
    /**
     * 应用分组id
     */
    var id: AppGroupId? = null

    /**
     * 应用编码
     */
    var code: String? = null

    /**
     * 应用分组名称
     */
    var name: String? = null

    /**
     * 接入密钥
     */
    var secret: String? = null

    /**
     * 创建人
     */
    var createdBy: String? = null

    /**
     * 创建时间
     */
    var createdAt: LocalDateTime? = null

    /**
     * 修改人
     */
    var updatedBy: String? = null

    /**
     * 修改时间
     */
    var updatedAt: LocalDateTime? = null
}