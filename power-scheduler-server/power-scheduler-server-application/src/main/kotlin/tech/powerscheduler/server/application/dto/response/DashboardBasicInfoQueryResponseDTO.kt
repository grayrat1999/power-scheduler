package tech.powerscheduler.server.application.dto.response

/**
 * @author grayrat
 * @since 2025/5/30
 */
data class DashboardBasicInfoQueryResponseDTO(
    /**
     * 在线worker数量
     */
    var onlineWorkerCount: Long,

    /**
     * 启用的任务数量
     */
    var enabledJobInfoCount: Long,

    /**
     * 禁用的任务数量
     */
    var disabledJobInfoCount: Long
) {
    val jobInfoCount: Long
        get() = enabledJobInfoCount + disabledJobInfoCount
}