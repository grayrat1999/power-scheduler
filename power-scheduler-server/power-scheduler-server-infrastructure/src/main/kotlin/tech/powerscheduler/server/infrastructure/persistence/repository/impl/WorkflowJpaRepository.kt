package tech.powerscheduler.server.infrastructure.persistence.repository.impl

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import tech.powerscheduler.server.infrastructure.persistence.model.WorkflowEntity

/**
 * @author grayrat
 * @since 2025/6/22
 */
interface WorkflowJpaRepository
    : JpaRepository<WorkflowEntity, Long>, JpaSpecificationExecutor<WorkflowEntity> {
}