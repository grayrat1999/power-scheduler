package tech.powerscheduler.server.infrastructure.persistence.repository.impl

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import tech.powerscheduler.server.infrastructure.persistence.model.WorkerRegistryEntity

/**
 * @author grayrat
 * @since 2025/4/29
 */
@Repository
interface WorkerRegistryJpaRepository :
    JpaRepository<WorkerRegistryEntity, Long>, JpaSpecificationExecutor<WorkerRegistryEntity> {

    fun countByNamespaceCode(namespaceCode: String): Long

    fun countByNamespaceCodeAndAppCode(namespaceCode: String, appCode: String): Long

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM WorkerRegistryEntity t WHERE t.id = :id")
    fun findByIdForUpdate(id: Long): WorkerRegistryEntity?

    fun findByAccessToken(accessToken: String): WorkerRegistryEntity?

}