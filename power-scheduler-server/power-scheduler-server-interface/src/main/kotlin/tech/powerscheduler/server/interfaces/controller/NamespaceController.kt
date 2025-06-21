package tech.powerscheduler.server.interfaces.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotNull
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import tech.powerscheduler.server.application.dto.request.NamespaceAddRequestDTO
import tech.powerscheduler.server.application.dto.request.NamespaceEditRequestDTO
import tech.powerscheduler.server.application.dto.request.NamespaceQueryRequestDTO
import tech.powerscheduler.server.application.service.NamespaceService

/**
 * @author grayrat
 * @since 2025/6/21
 */
@Tag(name = "NamespaceApi")
@Validated
@RestController
@RequestMapping("/api/namespace")
class NamespaceController(
    private val namespaceService: NamespaceService,
) : BaseController() {

    @Operation(summary = "查询命名空间列表")
    @GetMapping("/list")
    fun listNamespace(@Validated @NotNull param: NamespaceQueryRequestDTO) = wrapperResponse {
        return@wrapperResponse namespaceService.query(param)
    }

    @Operation(summary = "新增命名空间空间")
    @PostMapping("/add")
    fun addNamespace(@Validated @RequestBody param: NamespaceAddRequestDTO) = wrapperResponse {
        return@wrapperResponse namespaceService.add(param)
    }

    @Operation(summary = "编辑命名空间")
    @PostMapping("/edit")
    fun editNamespace(@Validated @RequestBody param: NamespaceEditRequestDTO) = wrapperResponse {
        return@wrapperResponse namespaceService.edit(param)
    }
}