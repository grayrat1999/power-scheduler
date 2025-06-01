package tech.powerscheduler.common.dto.request

import jakarta.validation.constraints.NotBlank

/**
 * worker心跳请求参数
 *
 * @author grayrat
 * @since 2025/5/20
 */
class WorkerHeartbeatRequestDTO {
    /**
     * 应用编码
     */
    @NotBlank
    var appCode: String? = null

    /**
     * 访问凭证
     */
    @NotBlank
    var accessToken: String? = null

    /**
     * worker网络地址
     */
    var host: String? = null

    /**
     * worker端口
     */
    @NotBlank
    var port: Int? = null
}
