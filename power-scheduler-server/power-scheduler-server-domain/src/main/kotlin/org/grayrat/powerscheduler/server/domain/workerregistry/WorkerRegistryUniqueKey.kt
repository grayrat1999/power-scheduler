package org.grayrat.powerscheduler.server.domain.workerregistry

/**
 * 任务注册记录的唯一键
 *
 * @author grayrat
 * @since 2025/5/21
 */
data class WorkerRegistryUniqueKey(
    val appCode: String,
    val host: String,
    val port: Int,
)