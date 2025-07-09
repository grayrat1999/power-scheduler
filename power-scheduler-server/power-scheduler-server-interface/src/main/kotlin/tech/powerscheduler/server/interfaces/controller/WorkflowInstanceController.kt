package tech.powerscheduler.server.interfaces.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotNull
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tech.powerscheduler.server.application.dto.request.WorkflowInstanceQueryRequestDTO
import tech.powerscheduler.server.application.service.WorkflowInstanceService

/**
 * @author grayrat
 * @since 2025/7/9
 */
@Tag(name = "WorkflowInstanceApi")
@Validated
@RestController
@RequestMapping("/api/WorkflowInstances")
class WorkflowInstanceController(
    private val workflowInstanceService: WorkflowInstanceService
) : BaseController() {

    @Operation(summary = "查询工作流实例列表")
    @GetMapping("/list")
    fun listWorkflowInstance(@Validated @NotNull param: WorkflowInstanceQueryRequestDTO?) = wrapperResponse {
        return@wrapperResponse workflowInstanceService.list(param!!)
    }

    @Operation(summary = "查询工作流实例详情")
    @GetMapping("/detail")
    fun getWorkflowInstance(@Validated @NotNull workflowInstanceId: Long?) = wrapperResponse {
        return@wrapperResponse workflowInstanceService.get(workflowInstanceId!!)
    }
}