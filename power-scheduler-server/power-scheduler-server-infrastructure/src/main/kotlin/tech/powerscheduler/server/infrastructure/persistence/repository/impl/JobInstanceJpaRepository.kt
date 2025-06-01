package tech.powerscheduler.server.infrastructure.persistence.repository.impl

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import tech.powerscheduler.common.enums.JobStatusEnum
import tech.powerscheduler.server.infrastructure.persistence.model.JobInstanceEntity
import java.time.LocalDateTime

/**
 * @author grayrat
 * @since 2025/4/16
 */
@Repository
interface JobInstanceJpaRepository :
    JpaRepository<JobInstanceEntity, Long>, JpaSpecificationExecutor<JobInstanceEntity> {

    @Query(
        """
        SELECT j.jobStatus, COUNT(j)
        FROM JobInstanceEntity j 
        WHERE TRUE
          AND (:appCode IS NULL OR j.appCode = :appCode)
          AND j.scheduleAt >= :scheduleAtRangeStart
          AND j.scheduleAt <= :scheduleAtRangeEnd
        GROUP BY j.jobStatus
    """
    )
    fun countGroupedByJobStatusWithAppCode(
        appCode: String?,
        scheduleAtRangeStart: LocalDateTime,
        scheduleAtRangeEnd: LocalDateTime,
    ): List<Array<Any>>

    @Query(
        """
        SELECT j.jobId, COUNT(j) 
        FROM JobInstanceEntity j 
        WHERE true 
            AND j.jobId IN :jobIds 
            AND j.jobStatus IN :jobStatuses 
        GROUP BY j.jobId
    """
    )
    fun countGroupByJobIdAndJobStatus(
        @Param("jobIds") jobIds: Iterable<Long>,
        @Param("jobStatuses") jobStatuses: Iterable<JobStatusEnum>
    ): List<Array<Any>>

    @EntityGraph(attributePaths = ["appGroupEntity"])
    @Query(
        """
        SELECT 
            jobInstance
        FROM JobInstanceEntity AS jobInstance
        JOIN JobInfoEntity AS jobInfo
            ON jobInfo.id = jobInstance.jobId
        WHERE true 
        AND jobInstance.jobId IN :jobIds 
        AND jobInstance.jobStatus IN :jobStatuses
    """
    )
    fun listDispatchable(
        jobIds: Iterable<Long>,
        jobStatuses: Iterable<JobStatusEnum>,
        pageRequest: Pageable
    ): Page<JobInstanceEntity>

}