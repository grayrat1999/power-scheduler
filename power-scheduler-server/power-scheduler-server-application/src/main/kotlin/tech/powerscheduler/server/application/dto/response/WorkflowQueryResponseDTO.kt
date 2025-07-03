package tech.powerscheduler.server.application.dto.response

/**
 * @author grayrat
 * @since 2025/6/23
 */
class WorkflowQueryResponseDTO {
    /**
     * 应用编码
     */
    var appName: String? = null

    /**
     * 主键
     */
    var id: Long? = null

    /**
     * 工作流名称
     */
    var name: String? = null

    /**
     * 调度类型
     */
    var scheduleType: ScheduleTypeDTO? = null

    /**
     * 调度配置
     */
    var scheduleConfig: String? = null

    /**
     * 调度配置描述
     */
    var scheduleConfigDesc: String? = null
}