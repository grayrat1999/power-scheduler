package tech.powerscheduler.server.interfaces.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tech.powerscheduler.server.application.context.UserContext
import tech.powerscheduler.server.application.dto.request.AppGroupAddRequestDTO
import tech.powerscheduler.server.application.dto.request.AppGroupQueryRequestDTO
import tech.powerscheduler.server.application.service.AppGroupService

/**
 * @author grayrat
 * @since 2025/4/16
 */
@Tag(name = "AppGroupApi")
@Validated
@RestController
@RequestMapping("/api/appGroups")
internal class AppGroupController(
    private var appGroupService: AppGroupService,
) : BaseController() {

    @Operation(summary = "查询应用分组")
    @PostMapping("/list")
    fun listAppGroup(@RequestBody @Validated param: AppGroupQueryRequestDTO) = wrapperResponse {
        return@wrapperResponse appGroupService.list(param)
    }

    @Operation(summary = "新增应用分组")
    @PostMapping("/add")
    fun addAppGroup(@RequestBody @Validated param: AppGroupAddRequestDTO) = wrapperResponse {
        return@wrapperResponse appGroupService.add(param, UserContext())
    }
}