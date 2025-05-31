package org.grayrat.powerscheduler.common.dto.request

import jakarta.validation.constraints.NotNull

/**
 * 任务终止请求参数
 *
 * @author grayrat
 * @since 2025/5/28
 */
class JobTerminateRequestDTO {
    /**
     * 任务实例id
     */
    @NotNull
    var jobInstanceId: Long? = null
}