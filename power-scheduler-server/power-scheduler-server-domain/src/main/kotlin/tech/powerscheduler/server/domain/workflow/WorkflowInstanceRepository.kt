package tech.powerscheduler.server.domain.workflow

import tech.powerscheduler.common.enums.JobStatusEnum

/**
 * @author grayrat
 * @since 2025/6/22
 */
interface WorkflowInstanceRepository {

    fun countByJobIdAndJobStatus(
        workflowIds: List<WorkflowId>,
        jobStatuses: Set<JobStatusEnum>,
    ): Map<WorkflowId, Long>

    fun save(workflowInstance: WorkflowInstance): WorkflowInstanceId
}