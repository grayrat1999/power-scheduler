package tech.powerscheduler.server.application.dto.request

/**
 * Cron表达式解析接口
 *
 * @author grayrat
 * @since 2025/4/16
 */
class CronParseRequestDTO {
    /**
     * Cron表达式
     */
    var cronExpression: String? = null

    /**
     * 下N次执行时间
     */
    var times: Int = 5
}