package tech.powerscheduler.server.infrastructure.persistence.model

import jakarta.persistence.*
import tech.powerscheduler.common.enums.*
import java.time.LocalDateTime

/**
 * @author grayrat
 * @since 2025/6/8
 */
@Entity
@Table(
    name = "task",
    indexes = [
        Index(name = "task_idx_job_id", columnList = "job_id"),
        Index(name = "task_idx_job_instance_id", columnList = "job_instance_id"),
        Index(name = "task_idx_worker_address", columnList = "worker_address"),
    ]
)
class TaskEntity : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    var id: Long? = null

    /**
     * 任务id
     */
    @Column(name = "job_id", nullable = false, updatable = false)
    var jobId: Long? = null

    /**
     * 任务实例id
     */
    @Column(name = "job_instance_id", nullable = false, updatable = false)
    var jobInstanceId: Long? = null

    /**
     * 应用编码
     */
    @Column(name = "app_code", nullable = false, updatable = false)
    var appCode: String? = null

    /**
     * 调度端ip
     */
    @Column(name = "scheduler_address")
    var schedulerAddress: String? = null

    /**
     * 执行器地址
     */
    @Column(name = "worker_address")
    var workerAddress: String? = null

    /**
     * 任务名称
     */
    @Column(name = "job_name", nullable = false, updatable = false)
    var jobName: String? = null

    /**
     * 任务类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    var jobType: JobTypeEnum? = null

    /**
     * 任务处理器
     */
    @Column(name = "processor", nullable = false, updatable = false)
    var processor: String? = null

    /**
     * 任务状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "job_status", nullable = false)
    var jobStatus: JobStatusEnum? = null

    /**
     * 触发时间
     */
    @Column(name = "schedule_at", nullable = false)
    var scheduleAt: LocalDateTime? = null

    /**
     * 开始时间
     */
    @Column(name = "start_at", insertable = false)
    var startAt: LocalDateTime? = null

    /**
     * 结束时间
     */
    @Column(name = "end_at", insertable = false)
    var endAt: LocalDateTime? = null

    /**
     * 任务参数
     */
    @Column(name = "execute_params", updatable = false)
    var executeParams: String? = null

    /**
     * 执行模式
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "execute_mode", nullable = false, updatable = false)
    var executeMode: ExecuteModeEnum? = null

    /**
     * 调度类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false, updatable = false)
    var scheduleType: ScheduleTypeEnum? = null

    /**
     * 任务信息
     */
    @Column(name = "message", insertable = false, length = 6000)
    var message: String? = null

    /**
     * 数据时间
     */
    @Column(name = "data_time", updatable = false)
    var dataTime: LocalDateTime? = null

    /**
     * 脚本类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "script_type")
    var scriptType: ScriptTypeEnum? = null

    /**
     * 脚本源代码
     */
    @Column(name = "script_code")
    var scriptCode: String? = null

    /**
     * 当前重试次数
     */
    @Column(name = "attempt_cnt", nullable = false)
    var attemptCnt: Int? = null

    /**
     * 最大重试次数
     */
    @Column(name = "max_attempt_cnt", nullable = false)
    var maxAttemptCnt: Int? = null

    /**
     * 优先级
     */
    @Column(name = "priority")
    var priority: Int? = null
}