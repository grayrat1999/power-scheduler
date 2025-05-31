package org.grayrat.powerscheduler.worker.job

import java.time.LocalDateTime

/**
 * 任务上下文
 *
 * @author grayrat
 * @since 2025/4/26
 */
open class JobContext {
    /**
     * 任务id
     */
    var jobId: Long? = null

    /**
     * 任务实例id
     */
    var jobInstanceId: Long? = null

    /**
     * 任务执行参数
     */
    var executeParams: String? = null

    /**
     * 数据时间
     */
    var dataTime: LocalDateTime? = null
}
