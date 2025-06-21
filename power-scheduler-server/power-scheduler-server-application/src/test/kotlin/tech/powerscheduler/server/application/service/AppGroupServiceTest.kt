package tech.powerscheduler.server.application.service

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import tech.powerscheduler.common.exception.BizException
import tech.powerscheduler.server.application.assembler.AppGroupAssembler
import tech.powerscheduler.server.application.context.UserContext
import tech.powerscheduler.server.application.dto.request.AppGroupAddRequestDTO
import tech.powerscheduler.server.application.dto.request.AppGroupQueryRequestDTO
import tech.powerscheduler.server.application.dto.response.AppGroupQueryResponseDTO
import tech.powerscheduler.server.domain.appgroup.AppGroup
import tech.powerscheduler.server.domain.appgroup.AppGroupId
import tech.powerscheduler.server.domain.appgroup.AppGroupQuery
import tech.powerscheduler.server.domain.appgroup.AppGroupRepository
import tech.powerscheduler.server.domain.common.Page
import tech.powerscheduler.server.domain.namespace.Namespace
import tech.powerscheduler.server.domain.namespace.NamespaceRepository

/**
 * @author grayrat
 * @since 2025/4/19
 */
class AppGroupServiceTest : FunSpec({

    val appGroupRepository = mockk<AppGroupRepository>()
    val appGroupAssembler = mockk<AppGroupAssembler>()
    val namespaceRepository = mockk<NamespaceRepository>()
    val appGroupService = AppGroupService(
        appGroupRepository = appGroupRepository,
        namespaceRepository = namespaceRepository,
        appGroupAssembler = appGroupAssembler,
    )

    context("AppGroupService#list") {
        test("should return correct PageDTO") {
            val appGroupQueryRequestDTO = AppGroupQueryRequestDTO()
            val query = AppGroupQuery()
            val appGroup1 = AppGroup()
            val appGroup2 = AppGroup()
            val appGroupQueryResponseDTO1 = AppGroupQueryResponseDTO()
            val appGroupQueryResponseDTO2 = AppGroupQueryResponseDTO()
            every { appGroupAssembler.toDomainQuery(appGroupQueryRequestDTO) } returns query
            every { appGroupRepository.pageQuery(query) } returns Page(content = listOf(appGroup1, appGroup2))
            every { appGroupAssembler.toAppGroupQueryResponseDTO(appGroup1) } returns appGroupQueryResponseDTO1
            every { appGroupAssembler.toAppGroupQueryResponseDTO(appGroup2) } returns appGroupQueryResponseDTO2
            val result = shouldNotThrowAny { appGroupService.list(appGroupQueryRequestDTO) }
            result.content shouldBe listOf(appGroupQueryResponseDTO1, appGroupQueryResponseDTO2)
        }
    }

    context("AppGroupService#add") {
        test("should throw BizException when appGroup exists") {
            val param = AppGroupAddRequestDTO().apply {
                this.namespaceCode = "namespaceCode"
                this.code = "code"
            }
            val appGroup = AppGroup()
            val namespace = Namespace()
            val userContext = UserContext()

            every { namespaceRepository.findByCode(param.namespaceCode!!) } returns namespace
            every { appGroupRepository.findByCode(namespace, param.code!!) } returns appGroup
            shouldThrow<BizException> { appGroupService.add(param, userContext) }
        }

        test("should succeed when appGroup not exists") {
            val param = AppGroupAddRequestDTO().apply {
                this.namespaceCode = "namespaceCode"
                this.code = "code"
            }
            val namespace = Namespace()
            val userContext = UserContext()

            val mockToDomainModelReturn = AppGroup()
            val mockSaveReturn = AppGroup().apply {
                this.id = AppGroupId(1L)
            }
            every { namespaceRepository.findByCode(param.namespaceCode!!) } returns namespace
            every { appGroupRepository.findByCode(namespace, param.code!!) } returns null
            every {
                appGroupAssembler.toDomainModel4AddRequest(
                    param,
                    any(),
                    userContext
                )
            } returns mockToDomainModelReturn
            every { appGroupRepository.save(mockToDomainModelReturn) } returns mockSaveReturn
            val result = shouldNotThrowAny { appGroupService.add(param, userContext) }
            result shouldBe mockSaveReturn.id?.value!!
        }
    }

})
