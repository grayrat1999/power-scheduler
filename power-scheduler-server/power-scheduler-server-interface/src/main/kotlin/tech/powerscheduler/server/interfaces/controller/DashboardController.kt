package tech.powerscheduler.server.interfaces.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import tech.powerscheduler.server.application.dto.request.DashboardBasicInfoQueryRequestDTO
import tech.powerscheduler.server.application.dto.request.DashboardStatisticsInfoQueryRequestDTO
import tech.powerscheduler.server.application.service.DashboardService

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
    fun queryBasicInfo(@Validated param: DashboardBasicInfoQueryRequestDTO?) = wrapperResponse {
        dashboardService.queryBasicInfo(param!!)
    }

    @Operation(summary = "查询统计信息")
    @PostMapping("/statisticsInfo")
    fun queryStatisticsInfo(
        @RequestBody @Validated param: DashboardStatisticsInfoQueryRequestDTO
    ) = wrapperResponse {
        dashboardService.queryStatisticsInfo(param)
    }
}