package tech.powerscheduler.server.infrastructure.persistence.repository.impl

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import tech.powerscheduler.server.infrastructure.persistence.model.SchedulerEntity

/**
 * @author grayrat
 * @since 2025/7/11
 */
interface SchedulerJpaRepository :
    JpaRepository<SchedulerEntity, Long>, JpaSpecificationExecutor<SchedulerEntity> {

    fun findByAddress(address: String): SchedulerEntity?

}