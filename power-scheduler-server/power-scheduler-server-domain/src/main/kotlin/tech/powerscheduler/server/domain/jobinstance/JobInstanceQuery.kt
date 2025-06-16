package tech.powerscheduler.server.domain.jobinstance

import tech.powerscheduler.common.enums.JobStatusEnum
import tech.powerscheduler.server.domain.common.PageQuery
import java.time.LocalDateTime

/**
 * 任务实例查询
 *
 * @author grayrat
 * @since 2025/4/19
 */
class JobInstanceQuery : PageQuery() {

    /**
     * 任务id
     */
    var jobId: Long? = null

    /**
     * 任务实例id
     */
    var jobInstanceId: Long? = null

    /**
     * 应用编码
     */
    var appCode: String? = null

    /**
     * 任务名称
     */
    var jobName: String? = null

    /**
     * 任务状态
     */
    var jobStatus: JobStatusEnum? = null

    /**
     * 开始时间区间
     */
    var startAtRange: Array<LocalDateTime>? = null

    /**
     * 结束时间区间
     */
    var endAtRange: Array<LocalDateTime>? = null
}