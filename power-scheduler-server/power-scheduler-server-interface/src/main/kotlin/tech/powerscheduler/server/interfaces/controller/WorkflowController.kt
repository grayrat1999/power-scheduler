package tech.powerscheduler.server.interfaces.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotNull
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import tech.powerscheduler.server.application.dto.request.WorkflowAddRequestDTO
import tech.powerscheduler.server.application.dto.request.WorkflowEditRequestDTO
import tech.powerscheduler.server.application.dto.request.WorkflowQueryRequestDTO
import tech.powerscheduler.server.application.service.WorkflowService

/**
 * @author grayrat
 * @since 2025/6/23
 */
@Tag(name = "WorkflowApi")
@Validated
@RestController
@RequestMapping("/api/workflows")
class WorkflowController(
    private val workflowService: WorkflowService,
) : BaseController() {

    @Operation(summary = "查询工作流列表")
    @GetMapping("/list")
    fun listWorkflow(@Validated @NotNull param: WorkflowQueryRequestDTO?) = wrapperResponse {
        workflowService.list(param!!)
    }

    @Operation(summary = "新增工作流")
    @PostMapping("/add")
    fun addWorkflow(@RequestBody @Validated @NotNull param: WorkflowAddRequestDTO?) = wrapperResponse {
        workflowService.add(param!!)
    }

    @Operation(summary = "编辑工作流")
    @PostMapping("/edit")
    fun editWorkflow(@RequestBody @Validated @NotNull param: WorkflowEditRequestDTO?) = wrapperResponse {
        workflowService.edit(param!!)
    }

    @Operation(summary = "删除工作流")
    @PostMapping("/delete")
    fun deleteWorkflow(@RequestBody @Validated @NotNull workflowId: Long?) = wrapperResponse {
        workflowService.delete(workflowId!!)
    }
}