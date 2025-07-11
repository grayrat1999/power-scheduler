package tech.powerscheduler.server.infrastructure.persistence.repository.impl

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import tech.powerscheduler.common.enums.WorkflowStatusEnum
import tech.powerscheduler.server.infrastructure.persistence.model.WorkflowInstanceEntity

/**
 * @author grayrat
 * @since 2025/6/22
 */
interface WorkflowInstanceJpaRepository
    : JpaRepository<WorkflowInstanceEntity, Long>, JpaSpecificationExecutor<WorkflowInstanceEntity> {

    @Query(
        """
        SELECT wi.workflowId, COUNT(wi) 
        FROM WorkflowInstanceEntity wi 
        WHERE true 
          AND wi.workflowId IN :workflowIds 
          AND wi.status IN :statuses 
        GROUP BY wi.workflowId
    """
    )
    fun countGroupByWorkflowIdAndStatus(
        workflowIds: List<Long>,
        statuses: Set<WorkflowStatusEnum>
    ): List<Array<Any>>


}