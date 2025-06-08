package tech.powerscheduler.server.infrastructure.persistence.repository.impl

import jakarta.persistence.LockModeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.*
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import tech.powerscheduler.server.infrastructure.persistence.model.JobInfoEntity

/**
 * @author grayrat
 * @since 2025/4/16
 */
@Repository
interface JobInfoJpaRepository :
    JpaRepository<JobInfoEntity, Long>, JpaSpecificationExecutor<JobInfoEntity> {

    @Query(
        """
        SELECT t.enabled, count(t) 
        FROM JobInfoEntity t 
        WHERE (:appCode IS NULL OR t.appCode = :appCode)
        GROUP BY t.enabled
    """
    )
    fun countGroupedByEnabledWithAppCode(appCode: String?): List<Array<Any>>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM JobInfoEntity t WHERE t.id = :id")
    fun findByIdForUpdate(id: Long): JobInfoEntity?

    @Query("SELECT t.id FROM JobInfoEntity t")
    fun findAllIds(pageable: Pageable): Page<Long>

    @Query(
        """
        SELECT t.id 
        FROM JobInfoEntity t 
        WHERE true
            AND (:enabled IS NULL OR t.enabled = :enabled) 
            AND t.schedulerAddress = :schedulerAddress
    """
    )
    fun findIdByEnabledAndSchedulerAddress(
        enabled: Boolean?,
        schedulerAddress: String,
        pageable: Pageable
    ): Page<Long>

    @Transactional
    @Modifying
    @Query(
        """
        UPDATE JobInfoEntity t
        SET t.schedulerAddress = null 
        WHERE true
            AND t.schedulerAddress = :schedulerAddress
    """
    )
    fun clearSchedulerAddress(schedulerAddress: String)

}