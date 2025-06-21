package tech.powerscheduler.common.dto.request

import jakarta.validation.constraints.NotBlank

/**
 * worker注册请求参数
 *
 * @author grayrat
 * @since 2025/5/20
 */
class WorkerRegisterRequestDTO {

    /**
     * 命名空间编码
     */
    @NotBlank
    var namespaceCode: String? = null

    /**
     * 应用编码
     */
    @NotBlank
    var appCode: String? = null

    /**
     * 应用密钥
     */
    @NotBlank
    var appSecret: String? = null

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
