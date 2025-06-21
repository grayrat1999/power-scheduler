package tech.powerscheduler.server.domain.job

import tech.powerscheduler.common.enums.JobStatusEnum
import tech.powerscheduler.server.domain.common.Page
import tech.powerscheduler.server.domain.common.PageQuery
import java.time.LocalDateTime

/**
 * 任务实例持计划仓库
 *
 * @author grayrat
 * @since 2025/4/16
 */
interface JobInstanceRepository {

    fun countGroupedByJobStatusWithAppCode(
        namespaceCode: String,
        appCode: String,
        scheduleAtRange: Array<LocalDateTime>
    ): Map<JobStatusEnum, Long>

    fun lockById(jobInstanceId: JobInstanceId): JobInstance?

    fun findById(jobInstanceId: JobInstanceId): JobInstance?

    fun pageQuery(query: JobInstanceQuery): Page<JobInstance>

    fun save(jobInstance: JobInstance): JobInstanceId

    fun saveAll(jobInstanceList: List<JobInstance>): List<JobInstanceId>

    fun countByJobIdAndJobStatus(
        jobIds: Iterable<JobId>,
        jobStatuses: Iterable<JobStatusEnum>
    ): Map<JobId, Long>

    fun listIdByJobIdAndJobStatus(
        jobId: JobId,
        jobStatuses: Iterable<JobStatusEnum>,
        pageQuery: PageQuery
    ): Page<JobInstanceId>

    fun listIdByJobIdAndJobStatusAndEndAtBefore(
        jobId: JobId,
        jobStatuses: Set<JobStatusEnum>,
        endAt: LocalDateTime,
        pageQuery: PageQuery
    ): Page<JobInstanceId>

    fun listDispatchable(
        jobIds: Iterable<JobId>,
        pageQuery: PageQuery
    ): Page<JobInstanceId>

    fun deleteByIds(ids: Iterable<JobInstanceId>)
}