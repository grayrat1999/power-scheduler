package tech.powerscheduler.server.application.dto.request

import jakarta.validation.constraints.NotNull

/**
 * @author grayrat
 * @since 2025/6/23
 */
class WorkflowNodeAddRequestDTO {
    /**
     * 工作流id
     */
    @NotNull
    var workflowId: Long? = null

}