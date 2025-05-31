package org.grayrat.powerscheduler.server.interfaces.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotNull
import org.grayrat.powerscheduler.server.application.dto.request.CronParseRequestDTO
import org.grayrat.powerscheduler.server.application.service.ToolService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author grayrat
 * @since 2025/5/30
 */
@Tag(name = "ToolApi")
@Validated
@RestController
@RequestMapping("/api/tool")
class ToolController(
    private val toolService: ToolService,
) : BaseController() {

    @Operation(summary = "解析CRON表达式")
    @GetMapping("/parseCron")
    fun parseCron(@NotNull param: CronParseRequestDTO?) = wrapperResponse {
        toolService.parseCron(param!!)
    }
}