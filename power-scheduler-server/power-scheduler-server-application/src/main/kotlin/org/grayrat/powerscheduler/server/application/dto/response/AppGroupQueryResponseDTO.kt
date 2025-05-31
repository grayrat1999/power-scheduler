package org.grayrat.powerscheduler.server.application.dto.response

import java.time.LocalDateTime

/**
 * 应用分组查询响应结果
 *
 * @author grayrat
 * @since 2025/4/16
 */
class AppGroupQueryResponseDTO {
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
}