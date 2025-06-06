package tech.powerscheduler.server.domain.jobinstance

import tech.powerscheduler.common.enums.*
import tech.powerscheduler.server.domain.appgroup.AppGroup
import tech.powerscheduler.server.domain.jobinfo.JobId
import java.time.LocalDateTime

/**
 * 任务实例
 *
 * @author grayrat
 * @since 2025/4/16
 */
class JobInstance {

    /**
     * 应用分组信息
     */
    var appGroup: AppGroup? = null

    /**
     * 主键
     */
    var id: JobInstanceId? = null

    /**
     * 任务id
     */
    var jobId: JobId? = null

    /**
     * 应用编码
     */
    var appCode: String? = null

    /**
     * 任务名称
     */
    var jobName: String? = null

    /**
     * 任务类型
     */
    var jobType: JobTypeEnum? = null

    /**
     * 任务处理器
     */
    var processor: String? = null

    /**
     * 任务状态
     */
    var jobStatus: JobStatusEnum? = null

    /**
     * 调度时间
     */
    var scheduleAt: LocalDateTime? = null

    /**
     * 开始时间
     */
    var startAt: LocalDateTime? = null

    /**
     * 结束时间
     */
    var endAt: LocalDateTime? = null

    /**
     * 任务参数
     */
    var executeParams: String? = null

    /**
     * 执行模式
     */
    var executeMode: ExecuteModeEnum? = null

    /**
     * 调度类型
     */
    var scheduleType: ScheduleTypeEnum? = null

    /**
     * 任务信息
     */
    var message: String? = null

    /**
     * 数据时间
     */
    var dataTime: LocalDateTime? = null

    /**
     * 脚本类型
     */
    var scriptType: ScriptTypeEnum? = null

    /**
     * 脚本源代码
     */
    var scriptCode: String? = null

    /**
     * 当前重试次数
     */
    var attemptCnt: Int? = null

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
     * 调度器地址
     */
    var schedulerAddress: String? = null

    /**
     * 执行器地址
     */
    var workerAddress: String? = null

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

    val canReattempt
        get() = this.attemptCnt!! < this.maxAttemptCnt!!

    fun resetStatusForReattempt() {
        this.scheduleAt = LocalDateTime.now().plusSeconds(this.attemptInterval?.toLong() ?: 15)
        this.startAt = null
        this.endAt = null
        this.attemptCnt = this.attemptCnt!! + 1
        this.jobStatus = JobStatusEnum.WAITING_DISPATCH
    }

    fun cloneForReattempt(): JobInstance {
        val newInstance = JobInstance().also {
            it.appGroup = this.appGroup
            it.jobId = this.jobId
            it.appCode = this.appCode
            it.jobName = this.jobName
            it.jobType = this.jobType
            it.processor = this.processor
            it.jobStatus = JobStatusEnum.WAITING_DISPATCH
            it.scheduleAt = LocalDateTime.now()
            it.executeParams = this.executeParams
            it.executeMode = this.executeMode
            it.scheduleType = this.scheduleType
            it.dataTime = this.dataTime
            it.scriptType = this.scriptType
            it.scriptCode = this.scriptCode
            it.attemptCnt = 0
            it.maxAttemptCnt = 0
            it.attemptInterval = this.attemptInterval
            it.priority = this.priority
        }

        return newInstance
    }

}