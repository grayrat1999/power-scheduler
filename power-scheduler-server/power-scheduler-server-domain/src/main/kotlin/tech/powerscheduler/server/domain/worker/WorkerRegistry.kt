package tech.powerscheduler.server.domain.worker

import java.time.LocalDateTime

/**
 * 任务注册记录
 *
 * @author grayrat
 * @since 2025/4/29
 */
class WorkerRegistry {
    /**
     * 主键
     */
    var id: WorkerRegistryId? = null

    /**
     * 应用编码
     */
    var appCode: String? = null

    /**
     * 服务地址
     */
    var host: String? = null

    /**
     * 服务端口
     */
    var port: Int? = null

    /**
     * 访问凭证
     */
    var accessToken: String? = null

    /**
     * 最后心跳时间
     */
    var lastHeartbeatAt: LocalDateTime? = null

    /**
     * 乐观锁
     */
    var version: Long? = null

    /**
     * CPU利用率
     */
    var cpuUsagePercent: Double? = null

    /**
     * 内存利用率
     */
    var memoryUsagePercent: Double? = null

    /**
     * 健康分
     */
    val healthScore: Double
        get() = (cpuUsagePercent ?: 0.0) * 0.5 + (memoryUsagePercent ?: 0.0) * 0.5

    val address
        get() = "$host:$port"
}