package tech.powerscheduler.server.infrastructure.persistence.repository

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import tech.powerscheduler.common.enums.JobStatusEnum
import tech.powerscheduler.common.enums.WorkflowStatusEnum
import tech.powerscheduler.server.domain.common.Page
import tech.powerscheduler.server.domain.workflow.*
import tech.powerscheduler.server.infrastructure.persistence.model.JobInstanceEntity
import tech.powerscheduler.server.infrastructure.persistence.model.WorkflowInstanceEntity
import tech.powerscheduler.server.infrastructure.persistence.repository.impl.WorkflowInstanceJpaRepository
import tech.powerscheduler.server.infrastructure.utils.toDomainModel
import tech.powerscheduler.server.infrastructure.utils.toDomainPage
import tech.powerscheduler.server.infrastructure.utils.toEntity

/**
 * @author grayrat
 * @since 2025/6/22
 */
@Repository
class WorkflowInstanceRepositoryImpl(
    private val workflowInstanceJpaRepository: WorkflowInstanceJpaRepository
) : WorkflowInstanceRepository {

    override fun countByJobIdAndJobStatus(
        workflowIds: List<WorkflowId>,
        jobStatuses: Set<JobStatusEnum>
    ): Map<WorkflowId, Long> {
        TODO("Not yet implemented")
    }

    override fun pageQuery(query: WorkflowInstanceQuery): Page<WorkflowInstance> {
        val pageable = PageRequest.of(
            query.pageNo - 1,
            query.pageSize,
            Sort.by(JobInstanceEntity::id.name).descending()
        )
        val specification = Specification<WorkflowInstanceEntity> { root, _, criteriaBuilder ->
            val statusEqual = criteriaBuilder.equal(
                root.get<WorkflowStatusEnum>(WorkflowInstanceEntity::status.name),
                query.status
            )
            criteriaBuilder.and(
                statusEqual,
            )
        }
        val page = workflowInstanceJpaRepository.findAll(specification, pageable)
        return page.map { it.toDomainModel() }.toDomainPage()
    }

    override fun findById(workflowInstanceId: WorkflowInstanceId): WorkflowInstance? {
        val entity = workflowInstanceJpaRepository.findByIdOrNull(workflowInstanceId.value)
        return entity?.toDomainModel()
    }

    override fun save(workflowInstance: WorkflowInstance): WorkflowInstanceId {
        val entity = workflowInstance.toEntity()
        workflowInstanceJpaRepository.save(entity)
        return WorkflowInstanceId(entity.id!!)
    }
}