package tech.powerscheduler.server.infrastructure.persistence.repository

import org.springframework.stereotype.Repository
import tech.powerscheduler.common.enums.JobStatusEnum
import tech.powerscheduler.server.domain.workflow.WorkflowId
import tech.powerscheduler.server.domain.workflow.WorkflowInstance
import tech.powerscheduler.server.domain.workflow.WorkflowInstanceId
import tech.powerscheduler.server.domain.workflow.WorkflowInstanceRepository

/**
 * @author grayrat
 * @since 2025/6/22
 */
@Repository
class WorkflowInstanceRepositoryImpl : WorkflowInstanceRepository {
    override fun countByJobIdAndJobStatus(
        workflowIds: List<WorkflowId>,
        jobStatuses: Set<JobStatusEnum>
    ): Map<WorkflowId, Long> {
        TODO("Not yet implemented")
    }

    override fun save(workflowInstance: WorkflowInstance): WorkflowInstanceId {
        TODO("Not yet implemented")
    }
}