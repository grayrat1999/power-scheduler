package tech.powerscheduler.common.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

/**
 * @author grayrat
 * @since 2025/6/16
 */
class WorkerMetricsReportRequestDTO {
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

    /**
     * CPU利用率
     */
    @NotNull
    @Positive
    var cpuUsagePercent: Double? = null

    /**
     * 内存利用率
     */
    @NotNull
    @Positive
    var memoryUsagePercent: Double? = null
}