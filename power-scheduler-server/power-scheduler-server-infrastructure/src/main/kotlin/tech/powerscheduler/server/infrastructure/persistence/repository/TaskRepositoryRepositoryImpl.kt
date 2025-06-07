package tech.powerscheduler.server.infrastructure.persistence.repository

import org.springframework.stereotype.Repository
import tech.powerscheduler.server.domain.task.Task
import tech.powerscheduler.server.domain.task.TaskId
import tech.powerscheduler.server.domain.task.TaskRepository

/**
 * @author grayrat
 * @since 2025/6/7
 */
@Repository
class TaskRepositoryRepositoryImpl : TaskRepository {

    override fun saveAll(taskList: List<Task>): List<TaskId> {
        TODO("Not yet implemented")
    }

}