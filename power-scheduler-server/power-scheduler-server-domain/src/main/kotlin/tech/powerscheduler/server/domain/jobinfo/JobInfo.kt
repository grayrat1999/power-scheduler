package tech.powerscheduler.server.domain.jobinfo

import tech.powerscheduler.common.enums.*
import tech.powerscheduler.common.enums.ScheduleTypeEnum.*
import tech.powerscheduler.common.exception.BizException
import tech.powerscheduler.server.domain.appgroup.AppGroup
import tech.powerscheduler.server.domain.jobinstance.JobInstance
import tech.powerscheduler.server.domain.utils.CronUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
     * 保留策略
     */
    var retentionPolicy: RetentionPolicyEnum? = null

    /**
     * 保留值
     */
    var retentionValue: Int? = null

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
        this.nextScheduleAt = when (scheduleType!!) {
            CRON -> CronUtils.nextExecution(scheduleConfig!!, LocalDateTime.now())

            FIX_RATE -> if (nextScheduleAt == null) {
                LocalDateTime.now()
            } else {
                LocalDateTime.now().plusSeconds(scheduleConfig!!.toLong())
            }

            FIX_DELAY -> if (nextScheduleAt == null) {
                LocalDateTime.now()
            } else {
                if (lastCompletedAt == null) {
                    LocalDateTime.now()
                } else {
                    lastCompletedAt!!.plusSeconds(scheduleConfig!!.toLong())
                }
            }

            ONE_TIME -> if (nextScheduleAt == null) {
                parseLocalDateTime(scheduleConfig)
            } else {
                nextScheduleAt
            }
        }
    }

    fun validScheduleConfig() {
        if (scheduleConfig.isNullOrBlank()) {
            throw BizException("任务[$id]的调度配置校验失败: 调度配置不能为空")
        }
        when (scheduleType) {
            CRON -> {
                if (CronUtils.isValidCron(scheduleConfig!!).not()) {
                    throw BizException("任务[$id]的调度配置校验失败: 非法cron表达式. 当前值=$scheduleConfig")
                }
            }

            FIX_RATE, FIX_DELAY -> {
                if (isPositiveNumber(scheduleConfig!!).not()) {
                    throw BizException("任务[$id]的调度配置校验失败: 调度配置不是正整数. 当前值=$scheduleConfig")
                }
            }

            ONE_TIME -> {
                if (isDateTimeText(scheduleConfig).not()) {
                    throw BizException("任务[$id]的调度配置校验失败: 调度配置不是 'yyyy-MM-dd HH:mm:ss'格式. 当前值=$scheduleConfig")
                }
            }

            null -> throw BizException("调度类型不能为null")
        }
    }

    private fun isDateTimeText(text: String?): Boolean {
        try {
            parseLocalDateTime(text)
            return true
        } catch (_: Exception) {
            return false
        }
    }

    private fun parseLocalDateTime(scheduleConfig: String?): LocalDateTime {
        val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return LocalDateTime.parse(scheduleConfig!!, pattern)
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