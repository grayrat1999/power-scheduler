package tech.powerscheduler.server.domain.worker

/**
 * 任务注册记录的唯一键
 *
 * @author grayrat
 * @since 2025/5/21
 */
data class WorkerRegistryUniqueKey(
    /**
     * 主机地址
     */
    val host: String,
    /**
     * 端口
     */
    val port: Int,
)