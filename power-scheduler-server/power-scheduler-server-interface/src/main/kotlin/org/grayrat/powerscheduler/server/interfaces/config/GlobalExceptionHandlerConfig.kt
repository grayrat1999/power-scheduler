package org.grayrat.powerscheduler.server.interfaces.config

import jakarta.validation.ConstraintViolationException
import org.grayrat.powerscheduler.common.dto.response.ResponseWrapper
import org.grayrat.powerscheduler.server.application.exception.BizException
import org.slf4j.LoggerFactory
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * @author grayrat
 * @since 2025/5/09
 */
@RestControllerAdvice
internal class GlobalExceptionHandlerConfig {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandlerConfig::class.java)

    @ExceptionHandler(BizException::class)
    fun handleBizExceptionException(e: BizException): ResponseWrapper<Unit> {
        val formatStackTraceInfo = e.stackTrace
            .filter { it.className.startsWith("org.grayrat") }
            .joinToString("\n") { "\tat $it" }
        val fullStackTraceInfo = "${e::class.java.name}: ${e.message}\n" + formatStackTraceInfo
        log.info("occurred BizException: {}\n{}", e.message, fullStackTraceInfo)
        return error(message = e.message)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationExceptionException(e: ConstraintViolationException): ResponseWrapper<Unit> {
        log.info("occurred ConstraintViolationException: {}", e.message)
        return error(message = e.message)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseWrapper<Unit> {
        val errorMessage = e.bindingResult.fieldErrors.first().let {
            val fieldName = it.field
            val message = it.defaultMessage ?: "非法"
            return@let "[$fieldName]字段$message"
        }
        log.info("occurred MethodArgumentNotValidException: {}", errorMessage)
        return error(message = errorMessage)
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(e: RuntimeException): ResponseWrapper<Unit> {
        log.error("occurred RuntimeException: {}", e.message, e)
        return error(message = e.message)
    }

    internal fun <T> error(message: String?) = ResponseWrapper<T>(
        data = null,
        success = false,
        code = "",
        message = message ?: ""
    )
}