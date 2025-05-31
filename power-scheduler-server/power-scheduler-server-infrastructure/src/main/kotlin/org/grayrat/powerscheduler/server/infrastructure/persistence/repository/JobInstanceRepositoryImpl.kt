package org.grayrat.powerscheduler.server.infrastructure.persistence.repository

import jakarta.persistence.criteria.JoinType
import org.grayrat.powerscheduler.common.enums.JobStatusEnum
import org.grayrat.powerscheduler.server.domain.common.Page
import org.grayrat.powerscheduler.server.domain.common.PageQuery
import org.grayrat.powerscheduler.server.domain.jobinfo.JobId
import org.grayrat.powerscheduler.server.domain.jobinstance.JobInstance
import org.grayrat.powerscheduler.server.domain.jobinstance.JobInstanceId
import org.grayrat.powerscheduler.server.domain.jobinstance.JobInstanceQuery
import org.grayrat.powerscheduler.server.domain.jobinstance.JobInstanceRepository
import org.grayrat.powerscheduler.server.infrastructure.persistence.model.AppGroupEntity
import org.grayrat.powerscheduler.server.infrastructure.persistence.model.JobInstanceEntity
import org.grayrat.powerscheduler.server.infrastructure.persistence.repository.impl.JobInstanceJpaRepository
import org.grayrat.powerscheduler.server.infrastructure.utils.toDomainModel
import org.grayrat.powerscheduler.server.infrastructure.utils.toDomainPage
import org.grayrat.powerscheduler.server.infrastructure.utils.toEntity
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
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

}