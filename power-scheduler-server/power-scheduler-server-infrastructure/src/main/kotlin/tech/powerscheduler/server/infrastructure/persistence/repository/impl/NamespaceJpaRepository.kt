package tech.powerscheduler.server.infrastructure.persistence.repository.impl

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import tech.powerscheduler.server.infrastructure.persistence.model.NamespaceEntity

/**
 * @author grayrat
 * @since 2025/6/21
 */
@Repository
interface NamespaceJpaRepository :
    JpaRepository<NamespaceEntity, Long>, JpaSpecificationExecutor<NamespaceEntity> {

    fun findByCode(code: String): NamespaceEntity?

}