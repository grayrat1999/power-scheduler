package tech.powerscheduler.worker

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * server发现服务
 * TODO: 支持从任意server获取所有可用的server节点
 *
 * @author grayrat
 * @since 2025/5/22
 */
class ServerDiscoveryService(
    private val seedNodes: Set<String>
) {
    /**
     * 当前可用的server地址
     */
    val availableServerUrls: MutableSet<String> = mutableSetOf()

    /**
     * http客户端
     */
    private val httpClient = PowerSchedulerWorkerHttpClient()

    /**
     * 调度线程池，用于定时获取可用的server节点
     */
    private val scheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(1)

    private val log = LoggerFactory.getLogger(ServerDiscoveryService::class.java)

    /**
     * 启动server发现服务
     */
    fun start() {
        checkServerAvailable(seedNodes)
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(
            { checkServerAvailable(seedNodes) },
            3,
            5,
            TimeUnit.SECONDS
        )
    }

    /**
     * 停止server发现服务
     */
    fun stop() {
        scheduledThreadPoolExecutor.shutdownNow()
        log.info("[PowerScheduler] {} stopped", javaClass.simpleName)
    }

    /**
     * 检查server是否可用
     *
     * @param servers 请求的server地址
     */
    @Synchronized
    fun checkServerAvailable(servers: Iterable<String>) {
        runBlocking {
            servers.map { server ->
                async { checkServerAvailable(server) }
            }.awaitAll()
        }
    }

    /**
     * 检查server是否可用，如果请求失败会自动重试，当达到重试次数的话，则认定当前server不可用
     *
     * @param server
     * @param attemptCnt
     */
    private suspend fun checkServerAvailable(server: String, attemptCnt: Int = 1) {
        val result = httpClient.checkServerAvailable(server)
        if (result.success && result.data == true) {
            availableServerUrls.add(server)
        } else {
            log.error(
                "[Powerscheduler] checkServerAvailable failed: server={}, msg={}, attemptCnt={}",
                server, result.message, attemptCnt
            )
            if (attemptCnt == 3) {
                availableServerUrls.remove(server)
                return
            } else {
                delay(1000)
                checkServerAvailable(server, attemptCnt + 1)
            }
        }
    }
}
