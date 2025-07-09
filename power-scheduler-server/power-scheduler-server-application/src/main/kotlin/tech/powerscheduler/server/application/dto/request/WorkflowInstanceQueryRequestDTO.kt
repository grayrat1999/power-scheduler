package tech.powerscheduler.server.application.dto.request

import tech.powerscheduler.common.dto.request.PageQueryRequestDTO
import tech.powerscheduler.common.enums.WorkflowStatusEnum

/**
 * @author grayrat
 * @since 2025/7/9
 */
class WorkflowInstanceQueryRequestDTO : PageQueryRequestDTO() {
    /**
     * 状态
     */
    var status: WorkflowStatusEnum? = null
}