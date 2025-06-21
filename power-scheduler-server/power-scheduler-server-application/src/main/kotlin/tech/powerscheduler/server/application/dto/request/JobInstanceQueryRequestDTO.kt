package tech.powerscheduler.server.application.dto.request

import tech.powerscheduler.common.dto.request.PageQueryRequestDTO
import tech.powerscheduler.common.enums.JobStatusEnum
import java.time.LocalDateTime

/**
 * 任务实例查询请求参数
 *
 * @author grayrat
 * @since 2025/4/16
 */
class JobInstanceQueryRequestDTO : PageQueryRequestDTO() {
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