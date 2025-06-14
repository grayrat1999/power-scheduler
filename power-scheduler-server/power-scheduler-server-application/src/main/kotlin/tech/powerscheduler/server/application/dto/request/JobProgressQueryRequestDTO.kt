package tech.powerscheduler.server.application.dto.request

import jakarta.validation.constraints.NotNull

/**
 * 任务进度查询参数
 *
 * @author grayrat
 * @since 2025/6/14
 */
class JobProgressQueryRequestDTO : PageQueryRequestDTO() {
    /**
     * 任务实例ID
     */
    @NotNull
    var jobInstanceId: Long? = null
}