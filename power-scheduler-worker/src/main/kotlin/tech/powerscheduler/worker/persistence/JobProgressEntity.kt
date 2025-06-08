package tech.powerscheduler.worker.persistence

import tech.powerscheduler.common.enums.JobStatusEnum
import java.time.LocalDateTime

/**
 * 任务进度实体模型
 *
 * @author grayrat
 * @since 2025/5/25
 */
class JobProgressEntity {
    var id: Long? = null
    var jobId: Long? = null
    var jobInstanceId: Long? = null
    var taskId: Long? = null
    var status: JobStatusEnum? = null
    var startAt: LocalDateTime? = null
    var endAt: LocalDateTime? = null
    var message: String? = null
}