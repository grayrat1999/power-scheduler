package org.grayrat.powerscheduler.server.application.service

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.grayrat.powerscheduler.server.application.assembler.AppGroupAssembler
import org.grayrat.powerscheduler.server.application.context.UserContext
import org.grayrat.powerscheduler.server.application.dto.request.AppGroupAddRequestDTO
import org.grayrat.powerscheduler.server.application.dto.request.AppGroupEditRequestDTO
import org.grayrat.powerscheduler.server.application.dto.request.AppGroupQueryRequestDTO
import org.grayrat.powerscheduler.server.application.dto.response.AppGroupQueryResponseDTO
import org.grayrat.powerscheduler.server.application.exception.BizException
import org.grayrat.powerscheduler.server.domain.appgroup.AppGroup
import org.grayrat.powerscheduler.server.domain.appgroup.AppGroupId
import org.grayrat.powerscheduler.server.domain.appgroup.AppGroupQuery
import org.grayrat.powerscheduler.server.domain.appgroup.AppGroupRepository
import org.grayrat.powerscheduler.server.domain.common.Page

/**
 * @author grayrat
 * @since 2025/4/19
 */
class AppGroupServiceTest : FunSpec({

    val appGroupRepository = mockk<AppGroupRepository>()
    val appGroupAssembler = mockk<AppGroupAssembler>()
    val appGroupService = AppGroupService(
        appGroupRepository = appGroupRepository,
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

    context("appGroupService#add") {
        test("should throw BizException when appGroup exists") {
            val param = AppGroupAddRequestDTO().apply {
                this.code = "code"
            }
            val userContext = UserContext()

            every { appGroupRepository.existsByCode(param.code!!) } returns true
            shouldThrow<BizException> { appGroupService.add(param, userContext) }
        }

        test("should succeed when appGroup exists") {
            val param = AppGroupAddRequestDTO().apply {
                this.code = "code"
            }
            val userContext = UserContext()

            val mockToDomainModelReturn = AppGroup()
            val mockSaveReturn = AppGroup().apply {
                this.id = AppGroupId(1L)
            }
            every { appGroupRepository.existsByCode(param.code!!) } returns false
            every { appGroupAssembler.toDomainModel4AddRequest(param, userContext) } returns mockToDomainModelReturn
            every { appGroupRepository.save(mockToDomainModelReturn) } returns mockSaveReturn
            val result = shouldNotThrowAny { appGroupService.add(param, userContext) }
            result shouldBe mockSaveReturn.id?.value!!
        }
    }

    context("appGroupService#edit") {
        test("should throw BizException when appGroup not exists") {
            val param = AppGroupEditRequestDTO().also {
                it.code = "code"
            }
            val userContext = UserContext()
            every { appGroupRepository.findByCode(any()) } returns null
            shouldThrow<BizException> { appGroupService.edit(param, userContext) }
        }

        test("should succeed when appGroup exists") {
            val param = AppGroupEditRequestDTO().also {
                it.code = "code"
            }
            val userContext = UserContext()
            val mockFindByCodeReturn = AppGroup()
            val mockToDomainModelReturn = AppGroup()
            val mockSaveReturn = AppGroup()

            every { appGroupRepository.findByCode(param.code!!) } returns mockFindByCodeReturn
            every {
                appGroupAssembler.toDomainModel4EditRequest(
                    mockFindByCodeReturn,
                    param,
                    userContext
                )
            } returns mockToDomainModelReturn
            every { appGroupRepository.save(mockToDomainModelReturn) } returns mockSaveReturn
            shouldNotThrowAny { appGroupService.edit(param, userContext) }
        }
    }

})
