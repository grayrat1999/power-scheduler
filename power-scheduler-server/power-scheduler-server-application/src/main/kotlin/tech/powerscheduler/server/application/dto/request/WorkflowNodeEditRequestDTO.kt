package tech.powerscheduler.server.application.dto.request

import jakarta.validation.constraints.NotNull

/**
 * @author grayrat
 * @since 2025/6/23
 */
class WorkflowNodeEditRequestDTO {
    /**
     * 工作流节点id
     */
    @NotNull
    var workflowNodeId: Long? = null
}