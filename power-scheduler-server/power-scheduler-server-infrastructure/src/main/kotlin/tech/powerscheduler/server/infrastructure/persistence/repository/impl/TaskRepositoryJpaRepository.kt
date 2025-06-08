package tech.powerscheduler.server.infrastructure.persistence.repository.impl

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import tech.powerscheduler.common.enums.JobStatusEnum
import tech.powerscheduler.server.infrastructure.persistence.model.TaskEntity

/**
 * @author grayrat
 * @since 2025/6/8
 */
@Repository
interface TaskRepositoryJpaRepository
    : JpaRepository<TaskEntity, Long>, JpaSpecificationExecutor<TaskEntity> {

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
    ): Page<TaskEntity>

    fun deleteByJobInstanceIdIn(jobInstanceIds: Iterable<Long>)

}