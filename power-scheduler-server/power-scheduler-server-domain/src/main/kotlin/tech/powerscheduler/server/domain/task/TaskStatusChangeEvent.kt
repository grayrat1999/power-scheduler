package tech.powerscheduler.server.domain.task

import tech.powerscheduler.common.enums.ExecuteModeEnum
import tech.powerscheduler.server.domain.jobinstance.JobInstanceId

/**
 * @author grayrat
 * @since 2025/6/8
 */
data class TaskStatusChangeEvent(
    val taskId: Long,
    val jobInstanceId: Long,
    val executeMode: ExecuteModeEnum,
) {
    companion object {
        fun create(
            taskId: TaskId,
            jobInstanceId: JobInstanceId,
            executeMode: ExecuteModeEnum,
        ): TaskStatusChangeEvent {
            return TaskStatusChangeEvent(
                taskId = taskId.value,
                jobInstanceId = jobInstanceId.value,
                executeMode = executeMode,
            )
        }
    }
}