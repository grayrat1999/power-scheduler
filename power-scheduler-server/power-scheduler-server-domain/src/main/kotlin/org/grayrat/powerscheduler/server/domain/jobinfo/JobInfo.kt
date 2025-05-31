package org.grayrat.powerscheduler.server.domain.jobinfo

import org.grayrat.powerscheduler.common.enums.*
import org.grayrat.powerscheduler.common.enums.ScheduleTypeEnum.*
import org.grayrat.powerscheduler.server.domain.appgroup.AppGroup
import org.grayrat.powerscheduler.server.domain.jobinstance.JobInstance
import org.grayrat.powerscheduler.server.domain.utils.CronUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * 任务信息
 *
 * @author grayrat
 * @since 2025/4/16
 */
class JobInfo {

    /**
     * 应用分组信息
     */
    var appGroup: AppGroup? = null

    /**
     * 主键
     */
    var id: JobId? = null

    /**
     * 应用编码
     */
    var appCode: String? = null

    /**
     * 任务名称
     */
    var jobName: String? = null

    /**
     * 任务描述
     */
    var jobDesc: String? = null

    /**
     * 任务类型
     */
    var jobType: JobTypeEnum? = null

    /**
     * 调度类型
     */
    var scheduleType: ScheduleTypeEnum? = null

    /**
     * 调度配置
     */
    var scheduleConfig: String? = null

    /**
     * 任务处理器
     */
    var processor: String? = null

    /**
     * 执行模式
     */
    var executeMode: ExecuteModeEnum? = null

    /**
     * 执行参数
     */
    var executeParams: String? = null

    /**
     * 下次执行时间
     */
    var nextScheduleAt: LocalDateTime? = null

    /**
     * 任务启用状态
     */
    var enabled: Boolean? = null

    /**
     * 任务并发数
     */
    var maxConcurrentNum: Int? = null

    /**
     * 脚本类型
     */
    var scriptType: ScriptTypeEnum? = null

    /**
     * 脚本源代码
     */
    var scriptCode: String? = null

    /**
     * 最大重试次数
     */
    var maxAttemptCnt: Int? = null

    /**
     * 重试间隔(s)
     */
    var attemptInterval: Int? = null

    /**
     * 优先级
     */
    var priority: Int? = null

    /**
     * 上次完成时间
     */
    var lastCompletedAt: LocalDateTime? = null

    /**
     * 调度器地址
     */
    var schedulerAddress: String? = null

    /**
     * 创建人
     */
    var createdBy: String? = null

    /**
     * 创建时间
     */
    var createdAt: LocalDateTime? = null

    /**
     * 修改人
     */
    var updatedBy: String? = null

    /**
     * 修改时间
     */
    var updatedAt: LocalDateTime? = null

    fun createInstance(): JobInstance {
        return JobInstance().also {
            it.appGroup = this.appGroup
            it.jobId = this.id
            it.jobName = this.jobName
            it.jobType = this.jobType
            it.processor = this.processor
            it.scheduleType = this.scheduleType
            it.executeMode = this.executeMode
            it.executeParams = this.executeParams
            it.appCode = this.appCode
            it.scriptType = this.scriptType
            it.scriptCode = this.scriptCode

            it.jobStatus = JobStatusEnum.WAITING_DISPATCH
            it.dataTime = LocalDateTime.now()
            it.attemptCnt = 0
            it.maxAttemptCnt = this.maxAttemptCnt ?: 1
            it.attemptInterval = this.attemptInterval
            it.priority = this.priority
            it.scheduleAt = this.nextScheduleAt
        }
    }

    fun updateNextScheduleTime() {
        validScheduleConfig()
        val nextScheduleAt = if (this.nextScheduleAt == null) {
            when (scheduleType) {
                CRON -> CronUtils.nextExecution(scheduleConfig!!, LocalDateTime.now())
                FIX_RATE -> LocalDateTime.now()
                FIX_DELAY -> LocalDateTime.now()
                ONE_TIME -> throw IllegalArgumentException("scheduleConfig can not be null when scheduleType is ONE_TIME")
                null -> throw IllegalArgumentException("scheduleType can not be null")
            }
        } else {
            when (scheduleType) {
                CRON -> CronUtils.nextExecution(scheduleConfig!!, LocalDateTime.now())

                FIX_RATE -> {
                    LocalDateTime.now().plusSeconds(scheduleConfig!!.toLong())
                }

                FIX_DELAY -> {
                    if (lastCompletedAt == null) {
                        LocalDateTime.now()
                    } else {
                        lastCompletedAt!!.plusSeconds(scheduleConfig!!.toLong())
                    }
                }

                ONE_TIME -> this.nextScheduleAt
                null -> throw IllegalArgumentException("scheduleType can not be null")
            }
        }
        this.nextScheduleAt = nextScheduleAt
    }

    fun validScheduleConfig() {
        if (scheduleConfig.isNullOrBlank()) {
            throw IllegalArgumentException("scheduleConfig can not be null or blank")
        }
        when (scheduleType) {
            CRON -> {
                if (CronUtils.isValidCron(scheduleConfig!!).not()) {
                    throw IllegalArgumentException("Invalid schedule config: $scheduleConfig")
                }
            }

            FIX_RATE -> {
                if (isPositiveNumber(scheduleConfig!!).not()) {
                    throw IllegalArgumentException("Invalid schedule config: $scheduleConfig, expect a positive number")
                }
            }

            FIX_DELAY -> {
                if (isPositiveNumber(scheduleConfig!!).not()) {
                    throw IllegalArgumentException("Invalid schedule config: $scheduleConfig, expect a positive number")
                }
            }

            ONE_TIME -> {
                val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                try {
                    pattern.parse(scheduleConfig!!)
                } catch (_: DateTimeParseException) {
                    throw IllegalArgumentException("Invalid schedule config format: $scheduleConfig, expect 'yyyy-MM-dd HH:mm:ss'")
                }
            }

            null -> throw IllegalArgumentException("scheduleType can not be null")
        }
    }

    private fun isPositiveNumber(s: String): Boolean {
        val number = try {
            s.toLong()
        } catch (_: NumberFormatException) {
            return false
        }
        return number > 0
    }
}