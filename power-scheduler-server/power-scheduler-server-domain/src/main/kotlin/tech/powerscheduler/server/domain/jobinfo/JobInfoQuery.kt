package tech.powerscheduler.server.domain.jobinfo

import tech.powerscheduler.server.domain.common.PageQuery

/**
 * 任务信息查询
 *
 * @author grayrat
 * @since 2025/4/19
 */
class JobInfoQuery : PageQuery() {
    /**
     * 应用编码
     */
    var appCode: String? = null

    /**
     * 任务名称
     */
    var jobName: String? = null

    /**
     * 任务处理器
     */
    var processor: String? = null
}