package tech.powerscheduler.server.infrastructure.persistence.repository

import jakarta.persistence.criteria.JoinType
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import tech.powerscheduler.common.enums.JobStatusEnum
import tech.powerscheduler.server.domain.common.Page
import tech.powerscheduler.server.domain.common.PageQuery
import tech.powerscheduler.server.domain.jobinfo.JobId
import tech.powerscheduler.server.domain.jobinstance.JobInstance
import tech.powerscheduler.server.domain.jobinstance.JobInstanceId
import tech.powerscheduler.server.domain.jobinstance.JobInstanceQuery
import tech.powerscheduler.server.domain.jobinstance.JobInstanceRepository
import tech.powerscheduler.server.infrastructure.persistence.model.AppGroupEntity
import tech.powerscheduler.server.infrastructure.persistence.model.JobInstanceEntity
import tech.powerscheduler.server.infrastructure.persistence.repository.impl.JobInstanceJpaRepository
import tech.powerscheduler.server.infrastructure.utils.toDomainModel
import tech.powerscheduler.server.infrastructure.utils.toDomainPage
import tech.powerscheduler.server.infrastructure.utils.toEntity
import java.time.LocalDateTime

/**
 * @author grayrat
 * @since 2025/4/16
 */
@Repository
class JobInstanceRepositoryImpl(
    private val jobInstanceJpaRepository: JobInstanceJpaRepository
) : JobInstanceRepository {

    override fun countGroupedByJobStatusWithAppCode(
        appCode: String?,
        scheduleAtRange: Array<LocalDateTime>
    ): Map<JobStatusEnum, Long> {
        val countResult = jobInstanceJpaRepository.countGroupedByJobStatusWithAppCode(
            appCode = appCode,
            scheduleAtRangeStart = scheduleAtRange[0],
            scheduleAtRangeEnd = scheduleAtRange[1],
        )
        return countResult.associate { it[0] as JobStatusEnum to it[1] as Long }
    }

    override fun findById(jobInstanceId: JobInstanceId): JobInstance? {
        val entity = jobInstanceJpaRepository.findByIdOrNull(jobInstanceId.value)
        return entity?.toDomainModel()
    }

    override fun pageQuery(query: JobInstanceQuery): Page<JobInstance> {
        val pageable = PageRequest.of(
            query.pageNo - 1, query.pageSize, Sort.by(JobInstanceEntity::id.name).descending()
        )
        val specification = Specification<JobInstanceEntity> { root, _, criteriaBuilder ->
            root.join<JobInstanceEntity, AppGroupEntity>(JobInstanceEntity::appGroupEntity.name, JoinType.LEFT)
            val jobInstanceIdEqual = query.jobInstanceId?.let {
                criteriaBuilder.equal(root.get<Long>(JobInstanceEntity::id.name), it)
            }
            val appGroupEqual = query.appCode.takeUnless { it.isNullOrBlank() }?.let {
                criteriaBuilder.equal(root.get<Long>(JobInstanceEntity::appCode.name), it)
            }
            val jobNameLike = query.jobName.takeUnless { it.isNullOrBlank() }?.let {
                criteriaBuilder.like(root.get(JobInstanceEntity::jobName.name), "%$it%")
            }
            val jobStatusEquals = query.jobStatus?.let {
                criteriaBuilder.equal(root.get<JobStatusEnum>(JobInstanceEntity::jobStatus.name), it)
            }
            val startAtBetween = query.startAtRange?.let {
                criteriaBuilder.between(root.get(JobInstanceEntity::startAt.name), it[0], it[1])
            }
            val endAtBetween = query.endAtRange?.let {
                criteriaBuilder.between(root.get(JobInstanceEntity::endAt.name), it[0], it[1])
            }
            val predicates = listOfNotNull(
                jobInstanceIdEqual, appGroupEqual, jobNameLike,
                jobStatusEquals, startAtBetween, endAtBetween,
            )
            criteriaBuilder.and(*predicates.toTypedArray())
        }
        val page = jobInstanceJpaRepository.findAll(specification, pageable)
        return page.map { it.toDomainModel() }.toDomainPage()
    }

    override fun save(jobInstance: JobInstance): JobInstanceId {
        val jobInstanceEntity = jobInstance.toEntity()
        jobInstanceJpaRepository.save(jobInstanceEntity)
        return JobInstanceId(jobInstanceEntity.id!!)
    }

    override fun saveAll(jobInstanceList: List<JobInstance>): List<JobInstanceId> {
        val entitiesToSave = jobInstanceList.map { it.toEntity() }
        jobInstanceJpaRepository.saveAll(entitiesToSave)
        return entitiesToSave.map { JobInstanceId(it.id!!) }
    }

    override fun countByJobIdAndJobStatus(
        jobIds: Iterable<JobId>,
        jobStatuses: Iterable<JobStatusEnum>
    ): Map<JobId, Long> {
        val group = jobInstanceJpaRepository.countGroupByJobIdAndJobStatus(
            jobIds = jobIds.map { it.value },
            jobStatuses = jobStatuses
        )
        return group.associate { JobId(it[0] as Long) to it[1] as Long }
    }

    override fun listIdByJobIdAndJobStatus(
        jobId: JobId,
        jobStatuses: Iterable<JobStatusEnum>,
        pageQuery: PageQuery,
    ): Page<JobInstanceId> {
        val pageable = PageRequest.of(
            pageQuery.pageNo - 1,
            pageQuery.pageSize,
            Sort.by(JobInstanceEntity::id.name).ascending()
        )
        val page = jobInstanceJpaRepository.listIdByJobIdAndJobStatus(
            jobId = jobId.value,
            jobStatuses = jobStatuses,
            pageable = pageable,
        )
        return page.map { JobInstanceId(it) }.toDomainPage()
    }

    override fun listIdByJobIdAndJobStatusAndEndAtBefore(
        jobId: JobId,
        jobStatuses: Set<JobStatusEnum>,
        endAt: LocalDateTime,
        pageQuery: PageQuery
    ): Page<JobInstanceId> {
        val pageable = PageRequest.of(
            pageQuery.pageNo - 1,
            pageQuery.pageSize,
            Sort.by(JobInstanceEntity::id.name).ascending()
        )
        val page = jobInstanceJpaRepository.listIdByJobIdAndJobStatusAndEndAtBefore(
            jobId = jobId.value,
            jobStatuses = jobStatuses,
            endAt = endAt,
            pageable = pageable,
        )
        return page.map { JobInstanceId(it) }.toDomainPage()
    }

    override fun listDispatchable(
        jobIds: Iterable<JobId>,
        pageQuery: PageQuery
    ): Page<JobInstance> {
        val pageable = PageRequest.of(
            pageQuery.pageNo - 1,
            pageQuery.pageSize,
            Sort.by(JobInstanceEntity::scheduleAt.name).ascending()
        )
        val page = jobInstanceJpaRepository.listDispatchable(
            jobIds = jobIds.map { it.value },
            jobStatuses = listOf(JobStatusEnum.WAITING_DISPATCH),
            pageRequest = pageable,
        )
        return page.map { it.toDomainModel() }.toDomainPage()
    }

    override fun findAllUncompletedByWorkerAddress(workerAddress: String): List<JobInstance> {
        val specification = Specification<JobInstanceEntity> { root, _, criteriaBuilder ->
            root.join<JobInstanceEntity, AppGroupEntity>(JobInstanceEntity::appGroupEntity.name, JoinType.LEFT)
            val workerAddressEqual = criteriaBuilder.equal(
                root.get<String>(JobInstanceEntity::workerAddress.name), workerAddress
            )
            val jobStatusIn = root.get<String>(JobInstanceEntity::jobStatus.name)
                .`in`(JobStatusEnum.UNCOMPLETED_STATUSES)
            criteriaBuilder.and(workerAddressEqual, jobStatusIn)
        }
        val list = jobInstanceJpaRepository.findAll(specification)
        return list.map { it.toDomainModel() }
    }

    override fun deleteByIds(ids: Iterable<JobInstanceId>) {
        jobInstanceJpaRepository.deleteAllByIdInBatch(ids.map { it.value })
    }
}