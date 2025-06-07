package tech.powerscheduler.server.domain.task

/**
 * @author grayrat
 * @since 2025/6/6
 */
interface TaskRepository {

    fun saveAll(taskList: Iterable<Task>): List<TaskId>

}