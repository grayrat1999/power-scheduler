package tech.powerscheduler.server.infrastructure.persistence.repository

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import tech.powerscheduler.common.enums.JobStatusEnum
import tech.powerscheduler.server.domain.common.Page
import tech.powerscheduler.server.domain.common.PageQuery
import tech.powerscheduler.server.domain.jobinfo.JobId
import tech.powerscheduler.server.domain.jobinstance.JobInstanceId
import tech.powerscheduler.server.domain.task.Task
import tech.powerscheduler.server.domain.task.TaskId
import tech.powerscheduler.server.domain.task.TaskRepository
import tech.powerscheduler.server.infrastructure.persistence.model.JobInstanceEntity
import tech.powerscheduler.server.infrastructure.persistence.model.TaskEntity
import tech.powerscheduler.server.infrastructure.persistence.repository.impl.TaskRepositoryJpaRepository
import tech.powerscheduler.server.infrastructure.utils.toDomainModel
import tech.powerscheduler.server.infrastructure.utils.toDomainPage
import tech.powerscheduler.server.infrastructure.utils.toEntity

/**
 * @author grayrat
 * @since 2025/6/7
 */
@Repository
class TaskRepositoryRepositoryImpl(
    private val taskRepositoryJpaRepository: TaskRepositoryJpaRepository,
) : TaskRepository {

    override fun findById(taskId: TaskId): Task? {
        val entity = taskRepositoryJpaRepository.findByIdOrNull(taskId.value)
        return entity?.toDomainModel()
    }

    override fun findAllByJobInstanceId(jobInstanceId: JobInstanceId): List<Task> {
        val entities = taskRepositoryJpaRepository.findByJobInstanceId(jobInstanceId.value)
        return entities.map { it.toDomainModel() }
    }

    override fun listDispatchable(
        jobIds: Iterable<JobId>,
        pageQuery: PageQuery
    ): Page<Task> {
        val pageable = PageRequest.of(
            pageQuery.pageNo - 1,
            pageQuery.pageSize,
            Sort.by(JobInstanceEntity::scheduleAt.name).ascending()
        )
        val page = taskRepositoryJpaRepository.listDispatchable(
            jobIds = jobIds.map { it.value },
            jobStatuses = listOf(JobStatusEnum.WAITING_DISPATCH),
            pageRequest = pageable,
        )
        return page.map { it.toDomainModel() }.toDomainPage()
    }

    override fun findAllUncompletedByWorkerAddress(workerAddress: String): List<Task> {
        val specification = Specification<TaskEntity> { root, _, criteriaBuilder ->
            val workerAddressEqual = criteriaBuilder.equal(
                root.get<String>(TaskEntity::workerAddress.name), workerAddress
            )
            val jobStatusIn = root.get<String>(TaskEntity::jobStatus.name)
                .`in`(JobStatusEnum.UNCOMPLETED_STATUSES)
            criteriaBuilder.and(workerAddressEqual, jobStatusIn)
        }
        val list = taskRepositoryJpaRepository.findAll(specification)
        return list.map { it.toDomainModel() }
    }

    override fun save(task: Task): TaskId {
        val entity = task.toEntity()
        taskRepositoryJpaRepository.save(entity)
        return TaskId(entity.id!!)
    }

    override fun saveAll(taskList: Iterable<Task>): List<TaskId> {
        val entities = taskList.map { it.toEntity() }
        taskRepositoryJpaRepository.saveAll(entities)
        return entities.map { TaskId(it.id!!) }
    }

    override fun deleteByJobInstanceId(jobInstanceIds: Iterable<JobInstanceId>) {
        taskRepositoryJpaRepository.deleteByJobInstanceIdIn(jobInstanceIds.map { it.value })
    }

}