package tech.powerscheduler.server.infrastructure.persistence.repository.impl

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import tech.powerscheduler.server.infrastructure.persistence.model.DomainEventEntity

/**
 * @author grayrat
 * @since 2025/6/8
 */
@Repository
interface DomainEventJpaRepository
    : JpaRepository<DomainEventEntity, Long>, JpaSpecificationExecutor<DomainEventEntity> {

}