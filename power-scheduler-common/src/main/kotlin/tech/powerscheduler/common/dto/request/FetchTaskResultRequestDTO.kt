package tech.powerscheduler.common.dto.request

import jakarta.validation.constraints.NotNull

/**
 * @author grayrat
 * @since 2025/6/19
 */
class FetchTaskResultRequestDTO : PageQueryRequestDTO() {
    /**
     * 任务实例id
     */
    @NotNull
    var jobInstanceId: Long? = null
}