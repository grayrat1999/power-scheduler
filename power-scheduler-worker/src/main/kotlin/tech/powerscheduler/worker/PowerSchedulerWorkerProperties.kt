package tech.powerscheduler.worker

import org.springframework.boot.context.properties.ConfigurationProperties
import java.io.InputStream
import java.util.*

/**
 * PowerSchedulerWorker的属性
 *
 * @author grayrat
 * @since 2025/5/22
 */
@ConfigurationProperties("power-scheduler-worker")
open class PowerSchedulerWorkerProperties {
    /**
     * 是否启用
     */
    var enabled: Boolean? = DEFAULT_ENABLED

    /**
     * 服务端地址
     */
    var serverEndpoint: Set<String>? = null

    /**
     * 应用编码
     */
    var appCode: String? = null

    /**
     * 应用密钥
     */
    var appSecret: String? = null

    /**
     * 服务监听端口
     */
    var port: Int? = DEFAULT_PORT

    /**
     * 外部请求地址
     */
    var externalHost: String? = null

    /**
     * 外部请求端口
     */
    var externalPort: Int? = null

    companion object {

        private const val DEFAULT_ENABLED = true
        private const val DEFAULT_PORT = 7758

        @JvmStatic
        fun load(inputStream: InputStream): PowerSchedulerWorkerProperties {
            val properties = Properties()
            properties.load(inputStream)
            return load(properties)
        }

        @JvmStatic
        fun load(properties: Properties): PowerSchedulerWorkerProperties {
            return PowerSchedulerWorkerProperties().apply {
                this.enabled = properties.getProperty("power-scheduler-worker.enabled")
                    ?.takeIf { it.isNotBlank() }
                    ?.toBoolean() ?: DEFAULT_ENABLED
                this.serverEndpoint = properties.getProperty("power-scheduler-worker.server-endpoint")
                    ?.takeIf { it.isNotBlank() }
                    ?.split(',')
                    ?.toSet()
                this.appCode = properties.getProperty("power-scheduler-worker.app-code")
                this.appSecret = properties.getProperty("power-scheduler-worker.app-secret")
                this.port = properties.getProperty("power-scheduler-worker.port")?.toInt() ?: DEFAULT_PORT
                this.externalHost = properties.getProperty("power-scheduler-worker.external-host")
                this.externalPort = properties.getProperty("power-scheduler-worker.external-port")?.toInt()
            }
        }

    }
}