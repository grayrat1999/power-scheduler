package tech.powerscheduler.worker

import org.slf4j.LoggerFactory
import tech.powerscheduler.worker.persistence.DataSourceManager
import tech.powerscheduler.worker.util.ClasspathUtil
import tech.powerscheduler.worker.util.executeSql
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 封装PowerSchedulerWorker的各个组件，提供服务启动，生命周期管理，优雅退出的功能
 * @author grayrat
 * @since 2025/5/7
 */
class PowerSchedulerWorker(
    /**
     * 是否启用
     */
    private val enabled: Boolean,
    /**
     * 服务端地址
     */
    private val endpoint: Set<String>,
    /**
     * 应用编码
     */
    private val appCode: String,
    /**
     * 应用密钥
     */
    private val appSecret: String,
    /**
     * 服务监听端口
     */
    private val port: Int,
    /**
     * 外部请求地址
     */
    private val externalHost: String?,
    /**
     * 外部请求端口
     */
    private val externalPort: Int?,
) {

    /**
     * 销毁标志, 用于避免spring的destroyHook与jvm的shutdownHook重复执行stop方法
     */
    private val destroyed = AtomicBoolean(false)

    private val log = LoggerFactory.getLogger(PowerSchedulerWorker::class.java)

    constructor(properties: PowerSchedulerWorkerProperties) : this(
        enabled = properties.enabled ?: true,
        endpoint = properties.serverEndpoint ?: emptySet(),
        appCode = properties.appCode ?: "",
        appSecret = properties.appSecret ?: "",
        port = properties.port!!,
        externalHost = properties.externalHost,
        externalPort = properties.externalPort,
    )

    private val serverDiscoveryService = ServerDiscoveryService(endpoint)

    private val workerRegisterService = WorkerRegisterService(
        appCode = appCode,
        appSecret = appSecret,
        host = externalHost,
        port = externalPort ?: port,
        serverDiscoveryService = serverDiscoveryService,
    )

    private val taskExecutorService = TaskExecutorService()

    private val taskProgressReportService = TaskProgressReportService(
        serverDiscoveryService = serverDiscoveryService,
        workerRegisterService = workerRegisterService,
    )

    private val embedServer = EmbedServer(
        port = port,
        taskExecutorService = taskExecutorService
    )

    fun init() {
        if (enabled.not()) {
            return
        }
        if (endpoint.isEmpty()) {
            throw IllegalArgumentException("[PowerScheduler] endpoint cannot be empty")
        }
        if (appCode.isBlank()) {
            throw IllegalArgumentException("[PowerScheduler] appCode is required")
        }
        if (appSecret.isBlank()) {
            throw IllegalArgumentException("[PowerScheduler] appSecret is required")
        }
        initDatabase()
        embedServer.start()
        taskExecutorService.start()
        serverDiscoveryService.start()
        taskProgressReportService.start()
        workerRegisterService.start()

        // 兼容非spring程序的优雅退出
        Runtime.getRuntime().addShutdownHook(Thread {
            destroy()
        })
    }

    fun destroy() {
        if (destroyed.compareAndSet(false, true)) {
            embedServer.stop()
            taskExecutorService.stop()
            taskProgressReportService.stop()
            workerRegisterService.stop()
            serverDiscoveryService.stop()
            DataSourceManager.closeDataSource()
            log.info("[PowerScheduler] {} stopped", javaClass.simpleName)
        } else {
            log.debug("[PowerScheduler] {} has stopped", javaClass.simpleName)
        }
    }

    private fun initDatabase() {
        val initSql = ClasspathUtil.readTextFrom("sql/power-scheduler-worker-init.sql")
        executeSql(initSql)
    }
}
