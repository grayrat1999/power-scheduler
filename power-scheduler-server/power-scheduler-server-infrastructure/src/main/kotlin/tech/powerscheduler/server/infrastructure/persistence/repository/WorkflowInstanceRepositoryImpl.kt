package tech.powerscheduler.server.infrastructure.persistence.repository

import org.springframework.stereotype.Repository
import tech.powerscheduler.common.enums.JobStatusEnum
import tech.powerscheduler.server.domain.workflow.WorkflowId
import tech.powerscheduler.server.domain.workflow.WorkflowInstance
import tech.powerscheduler.server.domain.workflow.WorkflowInstanceId
import tech.powerscheduler.server.domain.workflow.WorkflowInstanceRepository
import tech.powerscheduler.server.infrastructure.persistence.repository.impl.WorkflowInstanceJpaRepository
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

    override fun save(workflowInstance: WorkflowInstance): WorkflowInstanceId {
        val entity = workflowInstance.toEntity()
        workflowInstanceJpaRepository.save(entity)
        return WorkflowInstanceId(entity.id!!)
    }
}