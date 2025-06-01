package tech.powerscheduler.server.application.dto.response

import java.time.LocalDateTime

/**
 * @author grayrat
 * @since 2025/5/30
 */
class WorkerQueryResponseDTO {
    /**
     * 应用编码
     */
    var appCode: String? = null

    /**
     * ip地址
     */
    var host: String? = null

    /**
     * 端口
     */
    var port: Int? = null

    /**
     * 最后心跳时间
     */
    var lastHeartbeatAt: LocalDateTime? = null

    val address: String
        get() = "$host:$port"
}