package org.grayrat.powerscheduler.server.interfaces.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.grayrat.powerscheduler.server.application.dto.request.DashboardStatisticsInfoQueryRequestDTO
import org.grayrat.powerscheduler.server.application.service.DashboardService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * @author grayrat
 * @since 2025/5/20
 */
@Tag(name = "DashboardApi")
@Validated
@RestController
@RequestMapping("/api/dashboard")
internal class DashboardController(
    private val dashboardService: DashboardService,
) : BaseController() {

    @Operation(summary = "查询基本信息")
    @GetMapping("/basicInfo")
    fun queryBasicInfo(appCode: String?) = wrapperResponse {
        dashboardService.queryBasicInfo(appCode)
    }

    @Operation(summary = "查询统计信息")
    @PostMapping("/statisticsInfo")
    fun queryStatisticsInfo(
        @RequestBody @Validated param: DashboardStatisticsInfoQueryRequestDTO
    ) = wrapperResponse {
        dashboardService.queryStatisticsInfo(param)
    }
}