package tech.powerscheduler.server.domain.jobinstance

import tech.powerscheduler.common.enums.*
import tech.powerscheduler.common.enums.ExecuteModeEnum.*
import tech.powerscheduler.server.domain.appgroup.AppGroup
import tech.powerscheduler.server.domain.jobinfo.JobId
import tech.powerscheduler.server.domain.task.Task
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
     * 子任务最大重试次数
     */
    var taskMaxAttemptCnt: Int? = null

    /**
     * 子任务重试间隔(s)
     */
    var taskAttemptInterval: Int? = null

    /**
     * 优先级
     */
    var priority: Int? = null

    /**
     * worker地址(指定机器运行时使用)
     */
    var workerAddress: String? = null

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

    val batch: Int?
        get() = attemptCnt

    val canReattempt
        get() = this.attemptCnt!! < this.maxAttemptCnt!!

    fun markFailed(message: String? = null) {
        this.startAt = this.startAt ?: LocalDateTime.now()
        this.endAt = LocalDateTime.now()
        this.jobStatus = JobStatusEnum.FAILED
        this.message = message?.take(2000)
    }

    fun resetStatusForReattempt() {
        this.scheduleAt = LocalDateTime.now().plusSeconds(this.attemptInterval?.toLong() ?: 15)
        this.startAt = null
        this.endAt = null
        this.attemptCnt = this.attemptCnt!! + 1
        this.jobStatus = JobStatusEnum.WAITING_SCHEDULE
    }

    fun cloneForReattempt(): JobInstance {
        val newInstance = JobInstance().also {
            it.appGroup = this.appGroup
            it.jobId = this.jobId
            it.appCode = this.appCode
            it.jobName = this.jobName
            it.jobType = this.jobType
            it.processor = this.processor
            it.jobStatus = JobStatusEnum.WAITING_SCHEDULE
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

    fun createTask(workerAddress: String?): Task {
        val task = Task().also {
            it.jobId = this.jobId
            it.jobInstanceId = this.id
            it.appCode = this.appCode
            it.taskName = this.jobName
            it.jobType = this.jobType
            it.processor = this.processor
            it.taskStatus = JobStatusEnum.WAITING_DISPATCH
            it.scheduleAt = this.scheduleAt
            it.executeParams = this.executeParams
            it.executeMode = this.executeMode
            it.scheduleType = this.scheduleType
            it.message = this.message
            it.dataTime = this.dataTime
            it.scriptType = this.scriptType
            it.scriptCode = this.scriptCode
            it.priority = this.priority
            it.schedulerAddress = this.schedulerAddress
            it.workerAddress = if (this.executeMode == SINGLE && this.workerAddress.orEmpty().isNotEmpty()) {
                // 单机模式支持指定机器运行
                this.workerAddress
            } else {
                workerAddress
            }
            it.batch = this.batch
            it.attemptCnt = 0
            when (this.executeMode!!) {
                SINGLE -> {
                    it.maxAttemptCnt = 0
                    it.attemptInterval = 0
                }

                BROADCAST, MAP_REDUCE -> {
                    it.maxAttemptCnt = this.taskMaxAttemptCnt
                    it.attemptInterval = this.taskAttemptInterval
                }
            }
        }
        return task
    }

    fun calculateJobStatus(tasks: Iterable<Task>): JobStatusEnum {
        if (this.jobStatus in JobStatusEnum.COMPLETED_STATUSES) {
            return this.jobStatus!!
        }
        val maxBatch = tasks.maxOfOrNull { it.batch!! }
        return when (this.executeMode!!) {
            SINGLE -> tasks.first { it.batch == maxBatch }.taskStatus!!
            BROADCAST -> {
                val currentTasks = tasks.filter { it.batch == maxBatch }
                val jobStatusSet = currentTasks.map { it.taskStatus!! }.toSet()
                if ((jobStatusSet - JobStatusEnum.COMPLETED_STATUSES).isEmpty()) {
                    if (jobStatusSet.all { it == JobStatusEnum.SUCCESS }) {
                        return JobStatusEnum.SUCCESS
                    }
                    if (jobStatusSet.any { it == JobStatusEnum.FAILED }) {
                        return JobStatusEnum.FAILED
                    }
                    return JobStatusEnum.UNKNOWN
                } else {
                    return if (jobStatusSet.intersect(JobStatusEnum.COMPLETED_STATUSES).isNotEmpty()) {
                        JobStatusEnum.PROCESSING
                    } else {
                        jobStatusSet.maxBy { it.ordinal }
                    }
                }
            }

            MAP_REDUCE -> TODO()
        }
    }

    fun calculateStartAt(tasks: Iterable<Task>): LocalDateTime? {
        return tasks.asSequence()
            .mapNotNull { it.startAt }
            .minOrNull()
    }

    fun calculateEndAt(tasks: Iterable<Task>): LocalDateTime? {
        val maxBatch = tasks.maxOfOrNull { it.batch!! }
        return tasks.asSequence()
            .filter { it.batch == maxBatch }
            .mapNotNull { it.endAt }
            .maxOrNull()
    }

    fun calculateWorkerAddress(tasks: Iterable<Task>): String? {
        val maxBatch = tasks.maxOfOrNull { it.batch!! }
        // 只有单机模式需要将task中的worker地址回写
        if (this.executeMode == SINGLE) {
            return tasks.firstOrNull { it.batch == maxBatch }?.workerAddress
        }
        return null
    }

    fun terminate() {
        if (this.startAt == null) {
            this.startAt = LocalDateTime.now()
        }
        if (this.endAt == null) {
            this.endAt = LocalDateTime.now()
        }
        this.jobStatus = JobStatusEnum.CANCELED
    }
}