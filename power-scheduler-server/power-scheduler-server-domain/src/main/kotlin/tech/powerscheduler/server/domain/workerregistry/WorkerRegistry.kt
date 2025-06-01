package tech.powerscheduler.server.domain.workerregistry

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

    val address
        get() = "$host:$port"
}