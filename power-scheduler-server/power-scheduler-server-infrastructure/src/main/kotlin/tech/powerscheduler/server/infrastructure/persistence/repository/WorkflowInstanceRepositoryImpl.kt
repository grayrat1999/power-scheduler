package tech.powerscheduler.server.infrastructure.persistence.repository

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import tech.powerscheduler.common.enums.JobStatusEnum
import tech.powerscheduler.server.domain.common.Page
import tech.powerscheduler.server.domain.workflow.*
import tech.powerscheduler.server.infrastructure.persistence.repository.impl.WorkflowInstanceJpaRepository
import tech.powerscheduler.server.infrastructure.utils.toDomainModel
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
        TODO("Not yet implemented")
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