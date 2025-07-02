package tech.powerscheduler.server.interfaces.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotNull
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import tech.powerscheduler.server.application.dto.request.WorkflowNodeAddRequestDTO
import tech.powerscheduler.server.application.dto.request.WorkflowNodeEditRequestDTO
import tech.powerscheduler.server.application.dto.request.WorkflowNodeSaveDagRequestDTO
import tech.powerscheduler.server.application.service.WorkflowNodeService

/**
 * @author grayrat
 * @since 2025/6/23
 */
@Deprecated(message = "废弃, 合并到WorkflowController")
@Tag(name = "WorkflowNodeApi")
@Validated
@RestController
@RequestMapping("/api/workflowNodes")
class WorkflowNodeController(
    private val workflowNodeService: WorkflowNodeService,
) : BaseController() {

    @Deprecated(message = "废弃, 改用整体查询")
    @Operation(summary = "查询工作流节点列表")
    @GetMapping("/list")
    fun listWorkflow(@NotNull workflowId: Long?) = wrapperResponse {
        workflowNodeService.list(workflowId!!)
    }

    @Deprecated(message = "废弃, 改用整体保存")
    @Operation(summary = "新增工作流节点")
    @PostMapping("/add")
    fun addWorkflow(@RequestBody @Validated @NotNull param: WorkflowNodeAddRequestDTO?) = wrapperResponse {
        workflowNodeService.add(param!!)
    }

    @Deprecated(message = "废弃, 改用整体保存")
    @Operation(summary = "编辑工作流节点")
    @PostMapping("/edit")
    fun editWorkflow(@RequestBody @Validated @NotNull param: WorkflowNodeEditRequestDTO?) = wrapperResponse {
        workflowNodeService.edit(param!!)
    }

    @Deprecated(message = "废弃, 改用整体保存")
    @Operation(summary = "删除工作流节点")
    @PostMapping("/delete")
    fun deleteWorkflow(@RequestBody @Validated @NotNull workflowNodeId: Long?) = wrapperResponse {
        workflowNodeService.delete(workflowNodeId!!)
    }

    @Deprecated(message = "废弃, 改用整体保存")
    @Operation(summary = "保存有向无环图")
    @PostMapping("/saveDag")
    fun saveDag(@RequestBody @Validated @NotNull param: WorkflowNodeSaveDagRequestDTO?) = wrapperResponse {
        workflowNodeService.saveDag(param!!)
    }
}