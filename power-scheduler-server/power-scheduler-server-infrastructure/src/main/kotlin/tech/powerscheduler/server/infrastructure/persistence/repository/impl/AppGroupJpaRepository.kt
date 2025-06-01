package tech.powerscheduler.server.infrastructure.persistence.repository.impl

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import tech.powerscheduler.server.infrastructure.persistence.model.AppGroupEntity

/**
 * @author grayrat
 * @since 2025/4/16
 */
@Repository
interface AppGroupJpaRepository :
    JpaRepository<AppGroupEntity, Long>, JpaSpecificationExecutor<AppGroupEntity> {

    fun existsByCode(code: String): Boolean

    fun findByCode(code: String): AppGroupEntity?

}