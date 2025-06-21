package tech.powerscheduler.server.domain.task

import tech.powerscheduler.common.enums.TaskTypeEnum
import tech.powerscheduler.server.domain.common.Page
import tech.powerscheduler.server.domain.common.PageQuery
import tech.powerscheduler.server.domain.job.JobId
import tech.powerscheduler.server.domain.job.JobInstanceId

/**
 * @author grayrat
 * @since 2025/6/6
 */
interface TaskRepository {

    fun findById(taskId: TaskId): Task?

    fun findAllByJobInstanceId(jobInstanceId: JobInstanceId): List<Task>

    fun findAllByJobInstanceIdAndBatchAndTaskType(
        jobInstanceId: JobInstanceId,
        batch: Int,
        taskTypes: Collection<TaskTypeEnum>,
        pageQuery: PageQuery
    ): Page<Task>

    fun findAllByJobInstanceIdAndBatchAndTaskType(
        jobInstanceId: JobInstanceId,
        batch: Int,
    ): List<Task>

    fun listDispatchable(
        jobIds: Iterable<JobId>,
        pageQuery: PageQuery
    ): Page<Task>

    fun findAllUncompletedByWorkerAddress(workerAddress: String): List<Task>

    fun save(task: Task): TaskId

    fun saveAll(taskList: Iterable<Task>): List<TaskId>

    fun deleteByJobInstanceId(jobInstanceIds: Iterable<JobInstanceId>)
}