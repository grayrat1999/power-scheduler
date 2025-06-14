package tech.powerscheduler.server.infrastructure.service

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
import org.springframework.stereotype.Component
import tech.powerscheduler.common.api.DISPATCH_API
import tech.powerscheduler.common.api.TERMINATE_API
import tech.powerscheduler.common.api.WORKER_API_PREFIX
import tech.powerscheduler.common.dto.request.JobDispatchRequestDTO
import tech.powerscheduler.common.dto.request.JobTerminateRequestDTO
import tech.powerscheduler.common.dto.response.ResponseWrapper
import tech.powerscheduler.server.domain.worker.WorkerRemoteService

/**
 * @author grayrat
 * @since 2025/5/28
 */
@Component
class WorkerRemoteServiceImpl : WorkerRemoteService {

    private val log = LoggerFactory.getLogger(WorkerRemoteServiceImpl::class.java)

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

    override fun dispatch(baseUrl: String, param: JobDispatchRequestDTO): ResponseWrapper<Boolean> {
        val url = buildUrl(baseUrl, DISPATCH_API)
        return post<Boolean>(url, param)
    }

    override fun terminate(
        baseUrl: String,
        param: JobTerminateRequestDTO
    ): ResponseWrapper<Boolean> {
        val url = buildUrl(baseUrl, TERMINATE_API)
        return post<Boolean>(url, param)
    }

    fun buildUrl(baseUrl: String, uri: String): String {
        return "http://$baseUrl/${WORKER_API_PREFIX}/$uri"
    }
}