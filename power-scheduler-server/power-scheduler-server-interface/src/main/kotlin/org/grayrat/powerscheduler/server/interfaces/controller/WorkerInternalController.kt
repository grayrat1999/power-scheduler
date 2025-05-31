package org.grayrat.powerscheduler.server.interfaces.controller

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.constraints.NotNull
import org.grayrat.powerscheduler.common.api.*
import org.grayrat.powerscheduler.common.dto.request.JobProgressReportRequestDTO
import org.grayrat.powerscheduler.common.dto.request.WorkerHeartbeatRequestDTO
import org.grayrat.powerscheduler.common.dto.request.WorkerRegisterRequestDTO
import org.grayrat.powerscheduler.common.dto.request.WorkerUnregisterRequestDTO
import org.grayrat.powerscheduler.server.application.service.JobInstanceService
import org.grayrat.powerscheduler.server.application.service.WorkerLifeCycleService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * @author grayrat
 * @since 2025/5/20
 */
@Hidden
@Tag(name = "worker-internal")
@Validated
@RestController
@RequestMapping(SERVER_API_PREFIX)
internal class WorkerInternalController(
    private val jobInstanceService: JobInstanceService,
    private val workerLifeCycleService: WorkerLifeCycleService,
) : BaseController() {

    @PostMapping(REGISTER_API)
    fun register(
        @RequestBody @NotNull param: WorkerRegisterRequestDTO?,
        httpServletRequest: HttpServletRequest,
    ) = wrapperResponse {
        return@wrapperResponse workerLifeCycleService.register(
            param = param!!,
            remoteHost = httpServletRequest.remoteAddr,
        )
    }

    @PostMapping(UNREGISTER_API)
    fun unregister(
        @RequestBody @NotNull param: WorkerUnregisterRequestDTO?,
        httpServletRequest: HttpServletRequest,
    ) = wrapperResponse {
        workerLifeCycleService.unregister(
            param = param!!,
            remoteAddr = httpServletRequest.remoteAddr,
        )
        return@wrapperResponse true
    }

    @PostMapping(HEARTBEAT_API)
    fun heartbeat(
        @RequestBody @NotNull param: WorkerHeartbeatRequestDTO?,
        httpServletRequest: HttpServletRequest,
    ) = wrapperResponse {
        workerLifeCycleService.handleHeartbeat(
            param = param!!,
            remoteAddr = httpServletRequest.remoteAddr,
        )
        return@wrapperResponse true
    }

    @GetMapping(CHECK_HEALTH_API)
    fun checkHealth() = wrapperResponse {
        return@wrapperResponse true
    }

    @PostMapping(REPORT_PROGRESS_API)
    fun reportProgress(@RequestBody @NotNull param: JobProgressReportRequestDTO?) = wrapperResponse {
        jobInstanceService.updateProgress(param!!)
        return@wrapperResponse true
    }
}
