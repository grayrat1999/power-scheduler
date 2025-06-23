package tech.powerscheduler.server.infrastructure.persistence.repository.impl

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import tech.powerscheduler.server.infrastructure.persistence.model.WorkflowEntity

/**
 * @author grayrat
 * @since 2025/6/22
 */
interface WorkflowJpaRepository
    : JpaRepository<WorkflowEntity, Long>, JpaSpecificationExecutor<WorkflowEntity> {

    @Query(
        """
        SELECT w.id 
        FROM WorkflowEntity w 
        WHERE true
            AND (:enabled IS NULL OR w.enabled = :enabled) 
            AND w.schedulerAddress = :schedulerAddress
    """
    )
    fun listIdsByEnabledAndSchedulerAddress(
        enabled: Boolean?,
        schedulerAddress: String,
        pageable: Pageable
    ): Page<Long>
}