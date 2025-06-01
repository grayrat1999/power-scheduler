package tech.powerscheduler.server.application.dto.response

/**
 * @author grayrat
 * @since 2025/5/30
 */
data class DashboardStatisticsInfoQueryResponseDTO(
    /**
     * 调度数量
     */
    var jobInstanceCount: Long,

    /**
     * 执行成功数量
     */
    var succeedJobInstanceCount: Long,

    /**
     * 执行失败数量
     */
    var failedJobInstanceCount: Long,
)