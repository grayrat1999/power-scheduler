package org.grayrat.powerscheduler.server.application.assembler

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.grayrat.powerscheduler.server.application.context.UserContext
import org.grayrat.powerscheduler.server.application.dto.request.AppGroupAddRequestDTO
import org.grayrat.powerscheduler.server.application.dto.request.AppGroupEditRequestDTO
import org.grayrat.powerscheduler.server.application.dto.request.AppGroupQueryRequestDTO
import org.grayrat.powerscheduler.server.domain.appgroup.AppGroup
import org.grayrat.powerscheduler.server.domain.appgroup.AppGroupId
import java.time.LocalDateTime

class AppGroupAssemblerTest : FunSpec({

    val appGroupAssembler = AppGroupAssembler()

    context("test ${AppGroupAssembler::toDomainQuery.name}") {
        test("should assemble correct domain query") {
            val param = AppGroupQueryRequestDTO().also {
                it.code = "code"
                it.name = "name"
            }
            val result = shouldNotThrowAny {
                appGroupAssembler.toDomainQuery(param)
            }
            result.code shouldBe param.code
            result.name shouldBe param.name
        }
    }

    context("test ${AppGroupAssembler::toAppGroupQueryResponseDTO.name}") {
        test("should assemble correct DTO") {
            val appGroup = AppGroup().also {
                it.id = AppGroupId(1L)
                it.code = "code"
                it.name = "name"
                it.secret = "secret"
                it.createdBy = "user1"
                it.createdAt = LocalDateTime.now().minusDays(2)
                it.updatedBy = "user2"
                it.updatedAt = LocalDateTime.now().minusDays(1)
            }
            val result = shouldNotThrowAny {
                appGroupAssembler.toAppGroupQueryResponseDTO(appGroup)
            }
            result.code.shouldNotBeNull() shouldBeEqual appGroup.code!!
            result.name.shouldNotBeNull() shouldBeEqual appGroup.name!!
            result.secret.shouldNotBeNull() shouldBeEqual appGroup.secret!!
            result.createdBy.shouldNotBeNull() shouldBeEqual appGroup.createdBy!!
            result.createdAt.shouldNotBeNull() shouldBeEqual appGroup.createdAt!!
        }
    }

    context("test ${AppGroupAssembler::toDomainModel4AddRequest.name}") {
        test("should assemble correct domain model") {
            val appGroupAddRequestDTO = AppGroupAddRequestDTO().also {
                it.code = "code"
                it.name = "name"
            }
            val userContext = UserContext().also {
                it.userId = 1L
                it.userNo = "userNo"
                it.userName = "userName"
            }
            val result = shouldNotThrowAny {
                appGroupAssembler.toDomainModel4AddRequest(appGroupAddRequestDTO, userContext)
            }
            result.id.shouldBeNull()
            result.code.shouldNotBeNull() shouldBeEqual appGroupAddRequestDTO.code!!
            result.name.shouldNotBeNull() shouldBeEqual appGroupAddRequestDTO.name!!
            result.createdBy.shouldNotBeNull() shouldBeEqual userContext.userNo!!
            result.createdAt.shouldNotBeNull()
            result.updatedBy.shouldNotBeNull() shouldBeEqual userContext.userNo!!
            result.updatedAt.shouldNotBeNull()
        }
    }

    context("test ${AppGroupAssembler::toDomainModel4EditRequest.name}") {
        test("should assemble correct domain model") {
            val appGroup = AppGroup().also {
                it.id = AppGroupId(1L)
                it.code = "old-code"
                it.name = "old-name"
                it.createdBy = "old-createdBy"
                it.createdAt = LocalDateTime.now().minusMonths(1)
                it.updatedBy = "old-updatedBy"
                it.updatedAt = LocalDateTime.now().minusMonths(2)
            }
            val appGroupEditRequestDTO = AppGroupEditRequestDTO().also {
                it.code = "code"
                it.name = "name"
            }
            val userContext = UserContext().also {
                it.userId = 1L
                it.userNo = "userNo"
                it.userName = "userName"
            }
            val result = shouldNotThrowAny {
                appGroupAssembler.toDomainModel4EditRequest(
                    model = appGroup,
                    param = appGroupEditRequestDTO,
                    userContext = userContext
                )
            }
            result.id.shouldNotBeNull() shouldBeEqual appGroup.id!!
            result.code.shouldNotBeNull() shouldBeEqual appGroup.code!!
            result.name.shouldNotBeNull() shouldBeEqual appGroupEditRequestDTO.name!!
            result.createdBy.shouldNotBeNull() shouldBeEqual appGroup.createdBy!!
            result.createdAt.shouldNotBeNull() shouldBeEqual appGroup.createdAt!!
            result.updatedBy.shouldNotBeNull() shouldBeEqual userContext.userNo!!
            result.updatedAt.shouldNotBeNull() shouldNotBeEqual appGroup.updatedAt!!
        }
    }

})
