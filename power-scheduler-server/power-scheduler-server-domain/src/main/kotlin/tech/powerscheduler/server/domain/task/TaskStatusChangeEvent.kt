package tech.powerscheduler.server.domain.task

import tech.powerscheduler.common.enums.ExecuteModeEnum
import tech.powerscheduler.server.domain.jobinstance.JobInstanceId

/**
 * @author grayrat
 * @since 2025/6/8
 */
data class TaskStatusChangeEvent(
    val taskId: TaskId,
    val jobInstanceId: JobInstanceId,
    val executeMode: ExecuteModeEnum,
)