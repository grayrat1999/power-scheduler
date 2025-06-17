package tech.powerscheduler.worker

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import tech.powerscheduler.common.api.*
import tech.powerscheduler.common.dto.request.*
import tech.powerscheduler.common.dto.response.FetchTaskResultResponseDTO
import tech.powerscheduler.common.dto.response.PageDTO
import tech.powerscheduler.common.dto.response.ResponseWrapper

/**
 * http客户端，用于与server进行通信
 *
 * @author grayrat
 * @since 2025/5/22
 */
class PowerSchedulerWorkerHttpClient {

    private val log = LoggerFactory.getLogger(PowerSchedulerWorkerHttpClient::class.java)

    private val client: HttpClient = HttpClient(CIO) {
        expectSuccess = true
        install(HttpTimeout) {
            requestTimeoutMillis = 5_000
            connectTimeoutMillis = 4_000
            socketTimeoutMillis = 4_000
        }
        install(Logging) {
            this.level = LogLevel.NONE
        }
        install(ContentNegotiation) {
            jackson {
                registerModule(JavaTimeModule())
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            }
        }
    }

    private inline fun <reified R> get(url: String): ResponseWrapper<R> {
        return runBlocking {
            try {
                val result = client.get(url)
                return@runBlocking result.body()
            } catch (e: Exception) {
                log.error("[PowerScheduler] GET {} failed: {}", url, e.message, e)
                ResponseWrapper(
                    data = null,
                    success = false,
                    code = "",
                    message = e.message.orEmpty(),
                    cause = e,
                )
            }
        }
    }

    private inline fun <reified R> post(url: String, param: Any?): ResponseWrapper<R> {
        return runBlocking {
            try {
                val result = client.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(param)
                }
                return@runBlocking result.body()
            } catch (e: Exception) {
                log.error("[PowerScheduler] POST {} failed: {}", url, e.message, e)
                ResponseWrapper(
                    data = null,
                    success = false,
                    code = "",
                    message = e.message.orEmpty(),
                    cause = e,
                )
            }
        }
    }

    fun checkServerAvailable(baseUrl: String): ResponseWrapper<Boolean> {
        val url = buildUrl(baseUrl, CHECK_HEALTH_API)
        return get<Boolean>(url)
    }

    fun register(baseUrl: String, param: WorkerRegisterRequestDTO): ResponseWrapper<String> {
        val url = buildUrl(baseUrl, REGISTER_API)
        val result = post<String>(url, param)
        return result
    }

    fun heartbeat(baseUrl: String, param: WorkerHeartbeatRequestDTO): ResponseWrapper<Boolean> {
        val url = buildUrl(baseUrl, HEARTBEAT_API)
        val result = post<Boolean>(url, param)
        return result
    }

    fun unregister(baseUrl: String, param: WorkerUnregisterRequestDTO): ResponseWrapper<Boolean> {
        val url = buildUrl(baseUrl, UNREGISTER_API)
        val result = post<Boolean>(url, param)
        return result
    }

    fun reportProgress(baseUrl: String, param: TaskProgressReportRequestDTO): ResponseWrapper<Boolean> {
        val url = buildUrl(baseUrl, REPORT_PROGRESS_API)
        val result = post<Boolean>(url, param)
        return result
    }

    fun reportMetrics(baseUrl: String, param: WorkerMetricsReportRequestDTO): ResponseWrapper<Boolean> {
        val url = buildUrl(baseUrl, REPORT_METRICS_API)
        val result = post<Boolean>(url, param)
        return result
    }

    fun fetchTaskResult(
        baseUrl: String,
        param: FetchTaskResultRequestDTO
    ): ResponseWrapper<PageDTO<FetchTaskResultResponseDTO>> {
        val url = buildUrl(baseUrl, FETCH_TASK_RESULT)
        val result = post<PageDTO<FetchTaskResultResponseDTO>>(url, param)
        return result
    }

    fun buildUrl(baseUrl: String, uri: String): String {
        return "http://$baseUrl/$SERVER_API_PREFIX/$uri"
    }

}
