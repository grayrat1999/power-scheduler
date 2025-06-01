package tech.powerscheduler.server.application.service

import org.springframework.stereotype.Service
import tech.powerscheduler.common.exception.BizException
import tech.powerscheduler.server.application.dto.request.CronParseRequestDTO
import tech.powerscheduler.server.domain.utils.CronUtils
import java.time.LocalDateTime

/**
 * @author grayrat
 * @since 2025/5/30
 */
@Service
class ToolService {

    fun parseCron(param: CronParseRequestDTO): List<LocalDateTime> {
        val times = param.times
        val cronExpression = param.cronExpression.orEmpty()
        if (CronUtils.isValidCron(cronExpression).not()) {
            throw BizException("Invalid Cron expression: $cronExpression")
        }
        return generateSequence(LocalDateTime.now()) {
            CronUtils.nextExecution(
                cronExpression = cronExpression,
                baseTime = it
            )
        }.drop(1).take(times).toList()
    }

}