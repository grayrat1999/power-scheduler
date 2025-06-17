package tech.powerscheduler.worker.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

internal object JsonUtil {

    private val objectMapper = ObjectMapper()
        .registerKotlinModule()

    fun writeValueAsString(obj: Any?): String? {
        return objectMapper.writeValueAsString(obj)
    }

    fun <T> readValue(json: String?, clazz: Class<T>): T {
        return objectMapper.readValue(json, clazz)
    }
}