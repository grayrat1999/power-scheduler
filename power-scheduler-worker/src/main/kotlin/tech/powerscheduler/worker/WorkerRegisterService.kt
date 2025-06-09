package tech.powerscheduler.worker

import org.slf4j.LoggerFactory
import tech.powerscheduler.common.dto.request.WorkerHeartbeatRequestDTO
import tech.powerscheduler.common.dto.request.WorkerRegisterRequestDTO
import tech.powerscheduler.common.dto.request.WorkerUnregisterRequestDTO
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * worker注册服务
 *
 * @author grayrat
 * @since 2025/5/22
 */
class WorkerRegisterService(
    /**
     * 应用编码
     */
    val appCode: String,
    /**
     * 应用密钥
     */
    private val appSecret: String,
    /**
     * worker地址
     */
    private val host: String?,
    /**
     * worker端口
     */
    private val port: Int,
    /**
     * server发现服务
     */
    private val serverDiscoveryService: ServerDiscoveryService
) {

    /**
     * 调用注册接口成功后返回的访问凭证
     * 后续发送心跳和上报任务进度时，请求需要携带该访问凭证
     */
    @Volatile
    var accessToken: String = ""

    /**
     * 注册标志
     */
    private var registered = false

    /**
     * Http客户端
     */
    private val httpClient = PowerSchedulerWorkerHttpClient()

    /**
     * 调度线程池，用于注册和定时发送心跳
     */
    private val scheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(1)

    private val log = LoggerFactory.getLogger(WorkerRegisterService::class.java)

    /**
     * 启动注册服务
     */
    fun start() {
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(
            this::registerOrKeepAlive,
            0,
            1,
            TimeUnit.SECONDS
        )
    }

    /**
     * 停止注册服务
     */
    fun stop() {
        scheduledThreadPoolExecutor.shutdown()
        unregister()
    }

    /**
     * 注册worker，如果已经注册则发送心跳
     */
    fun registerOrKeepAlive() {
        if (registered) {
            heartbeat()
        } else {
            register()
        }
    }

    fun register() {
        val availableServerUrls = serverDiscoveryService.availableServerUrls
        val serverUrl = availableServerUrls.randomOrNull()
        if (serverUrl.isNullOrBlank()) {
            log.warn("[PowerScheduler] register failed, no available server")
            return
        }
        val param = WorkerRegisterRequestDTO().also {
            it.appCode = appCode
            it.appSecret = appSecret
            it.host = host
            it.port = port
        }
        val result = httpClient.register(serverUrl, param)
        if (result.success) {
            registered = true
            accessToken = result.data.orEmpty()
            log.info("[PowerScheduler] registered successfully")
        } else {
            registered = false
            log.error("[PowerScheduler] register fail: server={}, msg={}", serverUrl, result.message, result.cause)
        }
    }

    fun heartbeat() {
        val availableServerUrls = serverDiscoveryService.availableServerUrls
        val serverUrl = availableServerUrls.random()
        val param = WorkerHeartbeatRequestDTO().also {
            it.appCode = appCode
            it.accessToken = accessToken
            it.host = host
            it.port = port
        }
        val result = httpClient.heartbeat(serverUrl, param)
        if (result.success && result.data == true) {
            registered = true
        } else {
            registered = false
            log.error("[PowerScheduler] heartbeat failed: server={}, msg={}", serverUrl, result.message, result.cause)
        }
    }

    /**
     * 下线通知server
     */
    fun unregister() {
        registered = false
        scheduledThreadPoolExecutor.shutdown()
        val availableServerUrls = serverDiscoveryService.availableServerUrls
        if (availableServerUrls.isEmpty()) {
            log.info("[PowerScheduler] unregister failed, no available server")
            return
        }
        val serverUrl = availableServerUrls.random()
        val param = WorkerUnregisterRequestDTO().also {
            it.appCode = appCode
            it.accessToken = accessToken
            it.host = host
            it.port = port
        }
        val result = httpClient.unregister(serverUrl, param)
        if (result.success && result.data == true) {
            log.info("[PowerScheduler] unregister success")
        } else {
            log.error("[PowerScheduler] unregister failed: server={}, msg={}", serverUrl, result.message, result.cause)
        }

    }
}
