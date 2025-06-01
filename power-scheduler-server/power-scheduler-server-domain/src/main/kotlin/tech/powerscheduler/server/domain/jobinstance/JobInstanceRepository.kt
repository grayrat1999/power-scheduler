package tech.powerscheduler.server.domain.jobinstance

import tech.powerscheduler.common.enums.JobStatusEnum
import tech.powerscheduler.server.domain.common.Page
import tech.powerscheduler.server.domain.common.PageQuery
import tech.powerscheduler.server.domain.jobinfo.JobId
import java.time.LocalDateTime

/**
 * 任务实例持计划仓库
 *
 * @author grayrat
 * @since 2025/4/16
 */
interface JobInstanceRepository {

    fun countGroupedByJobStatusWithAppCode(
        appCode: String?,
        scheduleAtRange: Array<LocalDateTime>
    ): Map<JobStatusEnum, Long>

    fun findById(jobInstanceId: JobInstanceId): JobInstance?

    fun pageQuery(query: JobInstanceQuery): Page<JobInstance>

    fun save(jobInstance: JobInstance): JobInstanceId

    fun saveAll(jobInstanceList: List<JobInstance>): List<JobInstanceId>

    fun countByJobIdAndJobStatus(
        jobIds: Iterable<JobId>,
        jobStatuses: Iterable<JobStatusEnum>
    ): Map<JobId, Long>

    fun listDispatchable(
        jobIds: Iterable<JobId>,
        pageQuery: PageQuery
    ): Page<JobInstance>

    fun findAllUncompletedByWorkerAddress(workerAddress: String): List<JobInstance>

}