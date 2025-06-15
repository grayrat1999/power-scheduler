package tech.powerscheduler.server.domain.task

import tech.powerscheduler.server.domain.common.Page
import tech.powerscheduler.server.domain.common.PageQuery
import tech.powerscheduler.server.domain.jobinfo.JobId
import tech.powerscheduler.server.domain.jobinstance.JobInstanceId

/**
 * @author grayrat
 * @since 2025/6/6
 */
interface TaskRepository {

    fun findById(taskId: TaskId): Task?

    fun findAllByJobInstanceId(jobInstanceId: JobInstanceId): List<Task>

    fun findAllByJobInstanceIdAndBatch(
        jobInstanceId: JobInstanceId,
        batch: Int,
        pageQuery: PageQuery
    ): Page<Task>

    fun listDispatchable(
        jobIds: Iterable<JobId>,
        pageQuery: PageQuery
    ): Page<Task>

    fun findAllUncompletedByWorkerAddress(workerAddress: String): List<Task>

    fun save(task: Task): TaskId

    fun saveAll(taskList: Iterable<Task>): List<TaskId>

    fun deleteByJobInstanceId(jobInstanceIds: Iterable<JobInstanceId>)
}