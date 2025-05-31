package org.grayrat.powerscheduler.server.application.dto.request

/**
 * 任务查询请求参数
 *
 * @author grayrat
 * @since 2025/4/16
 */
class JobInfoQueryRequestDTO : PageQueryRequestDTO() {
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