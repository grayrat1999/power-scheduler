package tech.powerscheduler.server.domain.workflow

import tech.powerscheduler.common.enums.WorkflowStatusEnum
import tech.powerscheduler.server.domain.common.PageQuery

/**
 * @author grayrat
 * @since 2025/7/9
 */
class WorkflowInstanceQuery : PageQuery() {
    /**
     * 状态
     */
    var status: WorkflowStatusEnum? = null
}