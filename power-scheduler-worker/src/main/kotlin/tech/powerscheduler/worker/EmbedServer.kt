package tech.powerscheduler.worker

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory
import tech.powerscheduler.common.api.DISPATCH_API
import tech.powerscheduler.common.api.HEALTH_API
import tech.powerscheduler.common.api.TERMINATE_API
import tech.powerscheduler.common.api.WORKER_API_PREFIX
import tech.powerscheduler.common.dto.request.JobDispatchRequestDTO
import tech.powerscheduler.common.dto.request.JobTerminateRequestDTO
import tech.powerscheduler.common.dto.response.ResponseWrapper
import java.util.concurrent.TimeUnit

/**
 * worker内嵌的http服务，
 *
 * @author grayrat
 * @since 2025/5/26
 */
class EmbedServer(
    private val port: Int,
    private val taskExecutorService: TaskExecutorService
) {

    private val server = embeddedServer(
        CIO,
        host = "0.0.0.0",
        port = port,
        module = {
            configureRouting()
        }
    )

    private val log = LoggerFactory.getLogger(this::class.java)

    private fun Application.configureRouting() {
        install(ContentNegotiation) {
            jackson {
                registerModule(JavaTimeModule())
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            }
        }
        routing {
            route(WORKER_API_PREFIX) {
                get(HEALTH_API) {
                    call.respond(mapOf("status" to "UP"))
                }
                post(DISPATCH_API) {
                    val param: JobDispatchRequestDTO? = this.call.receive()
                    val responseWrapper = wrapperResponse {
                        taskExecutorService.schedule(param!!)
                        return@wrapperResponse true
                    }
                    call.respond(responseWrapper)
                }
                post(TERMINATE_API) {
                    val param: JobTerminateRequestDTO? = this.call.receive()
                    val responseWrapper = wrapperResponse {
                        taskExecutorService.terminate(param!!)
                        return@wrapperResponse true
                    }
                    call.respond(responseWrapper)
                }
            }
        }
    }

    /**
     * 开启http服务
     */
    fun start() {
        log.info("embedServer started on port {}", port)
        server.start(false)
    }

    /**
     * 关闭http服务
     */
    fun stop() {
        server.stop(1, 1, TimeUnit.SECONDS)
        log.info("[PowerScheduler] {} stopped", javaClass.simpleName)
    }

    private fun <T> wrapperResponse(block: () -> T?): ResponseWrapper<T> {
        try {
            val result = block()
            return success(result)
        } catch (e: Exception) {
            log.error("", e)
            return error(message = e.message)
        }
    }

    private fun <T> success(data: T?) = ResponseWrapper(
        data = data,
        success = true,
        code = "0",
        message = "success",
    )

    private fun <T> error(code: String = "500", message: String?) = ResponseWrapper<T>(
        data = null,
        success = false,
        code = code,
        message = message ?: ""
    )
}