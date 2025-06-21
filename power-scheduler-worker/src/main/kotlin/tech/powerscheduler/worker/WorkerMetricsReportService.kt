package tech.powerscheduler.worker

import org.slf4j.LoggerFactory
import tech.powerscheduler.common.dto.request.WorkerMetricsReportRequestDTO
import tech.powerscheduler.worker.util.SystemMetricsCollector
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * 系统指标上报服务
 *
 * @author grayrat
 * @since 2025/6/16
 */
class WorkerMetricsReportService(
    /**
     * worker地址
     */
    private val host: String?,
    /**
     * worker端口
     */
    private val port: Int,
    /**
     * worker注册服务
     */
    private val workerRegisterService: WorkerRegisterService,
    /**
     * server发现服务
     */
    private val serverDiscoveryService: ServerDiscoveryService,
) {
    /**
     * Http客户端
     */
    private val httpClient = PowerSchedulerWorkerHttpClient()

    /**
     * 调度线程池，用于注册和定时发送心跳
     */
    private val scheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(1)

    private val log = LoggerFactory.getLogger(WorkerMetricsReportService::class.java)

    /**
     * 启用系统指标上报服务
     */
    fun start() {
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(
            this::reportMetrics,
            0,
            10,
            TimeUnit.SECONDS
        )
    }

    fun stop() {
        scheduledThreadPoolExecutor.shutdownNow()
    }

    fun reportMetrics() {
        val availableServerUrls = serverDiscoveryService.availableServerUrls
        val serverUrl = availableServerUrls.randomOrNull()
        if (serverUrl.isNullOrBlank()) {
            log.warn("[PowerScheduler] register failed, no available server")
            return
        }
        val param = WorkerMetricsReportRequestDTO().also {
            it.accessToken = workerRegisterService.accessToken
            it.host = host
            it.port = port
            it.cpuUsagePercent = SystemMetricsCollector.getSystemCpuLoad()
            it.memoryUsagePercent = SystemMetricsCollector.getJvmMemoryUsage()
        }
        val result = httpClient.reportMetrics(serverUrl, param)
        if (result.success) {
            log.debug("[PowerScheduler] reportMetrics successfully")
        } else {
            log.warn("[PowerScheduler] reportMetrics fail: server={}, msg={}", serverUrl, result.message, result.cause)
        }
    }

}