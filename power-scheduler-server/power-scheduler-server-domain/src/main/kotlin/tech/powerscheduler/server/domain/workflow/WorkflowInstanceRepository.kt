package tech.powerscheduler.server.domain.workflow

import tech.powerscheduler.common.enums.JobStatusEnum
import tech.powerscheduler.server.domain.common.Page

/**
 * @author grayrat
 * @since 2025/6/22
 */
interface WorkflowInstanceRepository {

    fun countByJobIdAndJobStatus(
        workflowIds: List<WorkflowId>,
        jobStatuses: Set<JobStatusEnum>,
    ): Map<WorkflowId, Long>

    fun pageQuery(query: WorkflowInstanceQuery): Page<WorkflowInstance>

    fun findById(workflowInstanceId: WorkflowInstanceId): WorkflowInstance?

    fun save(workflowInstance: WorkflowInstance): WorkflowInstanceId
}