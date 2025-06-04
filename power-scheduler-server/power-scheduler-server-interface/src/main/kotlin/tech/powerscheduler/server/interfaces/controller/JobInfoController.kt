package tech.powerscheduler.server.interfaces.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotNull
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import tech.powerscheduler.common.enums.JobTypeEnum
import tech.powerscheduler.common.exception.BizException
import tech.powerscheduler.server.application.dto.request.*
import tech.powerscheduler.server.application.service.JobInfoService
import tech.powerscheduler.server.application.service.JobInstanceService

/**
 * @author grayrat
 * @since 2025/4/16
 */
@Tag(name = "JobInfoApi")
@Validated
@RestController
@RequestMapping("/api/jobInfos")
internal class JobInfoController(
    private val jobInfoService: JobInfoService,
    private val jobInstanceService: JobInstanceService,
) : BaseController() {

    @Operation(summary = "查询任务列表")
    @PostMapping("/list")
    fun listJobInfo(@Validated @RequestBody @NotNull param: JobInfoQueryRequestDTO) = wrapperResponse {
        return@wrapperResponse jobInfoService.query(param)
    }

    @Operation(summary = "查询任务详情")
    @GetMapping("/detail")
    fun getJobInfo(@Validated @NotNull jobId: Long?) = wrapperResponse {
        return@wrapperResponse jobInfoService.detail(jobId!!)
    }

    @Operation(summary = "新增任务")
    @PostMapping("/add")
    fun addJobInfo(@Validated @RequestBody param: JobInfoAddRequestDTO) = wrapperResponse {
        if (param.jobType != JobTypeEnum.SCRIPT && param.processor.isNullOrBlank()) {
            throw BizException("任务处理器不能为空")
        }
        return@wrapperResponse jobInfoService.add(param)
    }

    @Operation(summary = "编辑任务")
    @PostMapping("/edit")
    fun editJobInfo(@Validated @RequestBody param: JobInfoEditRequestDTO) = wrapperResponse {
        if (param.jobType != JobTypeEnum.SCRIPT && param.processor.isNullOrBlank()) {
            throw BizException("任务处理器不能为空")
        }
        return@wrapperResponse jobInfoService.edit(param)
    }

    @Operation(summary = "修改任务启用状态")
    @PostMapping("/switch")
    fun switchEnable(@Validated @RequestBody param: JobSwitchRequestDTO) = wrapperResponse {
        return@wrapperResponse jobInfoService.switch(param)
    }

    @Operation(summary = "删除任务")
    @PostMapping("/remove")
    fun removeJobInfo(@NotNull jobId: Long?) = wrapperResponse {
        return@wrapperResponse jobInfoService.remove(jobId!!)
    }

    @Operation(summary = "运行任务")
    @PostMapping("/run")
    fun run(@Validated @RequestBody param: JobRunRequestDTO) = wrapperResponse {
        return@wrapperResponse jobInstanceService.run(param)
    }
}