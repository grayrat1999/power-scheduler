package org.grayrat.powerscheduler.server.domain.utils

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import java.time.LocalDateTime
import java.time.ZoneId


/**
 * CRON工具类
 *
 * @author grayrat
 * @since 2025/4/28
 */
object CronUtils {

    val parser = CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING53))

    fun isValidCron(cronExpression: String): Boolean {
        try {
            parser.parse(cronExpression).validate()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun nextExecution(cronExpression: String, baseTime: LocalDateTime): LocalDateTime {
        val baseZonedTime = baseTime.atZone(ZoneId.systemDefault())
        val cron = parser.parse(cronExpression)
        val executionTime = ExecutionTime.forCron(cron)
        val nextExecution = executionTime.nextExecution(baseZonedTime)
        return nextExecution.map { it.toLocalDateTime() }.orElseThrow()!!
    }

}