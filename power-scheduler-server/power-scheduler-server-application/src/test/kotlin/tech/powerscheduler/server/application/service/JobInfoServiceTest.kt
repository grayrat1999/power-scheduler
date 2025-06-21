package tech.powerscheduler.server.application.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import tech.powerscheduler.server.application.assembler.JobInfoAssembler
import tech.powerscheduler.server.application.dto.request.JobInfoQueryRequestDTO
import tech.powerscheduler.server.application.dto.response.JobInfoDetailResponseDTO
import tech.powerscheduler.server.application.dto.response.JobInfoQueryResponseDTO
import tech.powerscheduler.server.domain.appgroup.AppGroupRepository
import tech.powerscheduler.server.domain.common.Page
import tech.powerscheduler.server.domain.job.JobInfo
import tech.powerscheduler.server.domain.job.JobInfoQuery
import tech.powerscheduler.server.domain.job.JobInfoRepository
import tech.powerscheduler.server.domain.namespace.NamespaceRepository

/**
 * @author grayrat
 * @since 2025/4/19
 */
class JobInfoServiceTest : FunSpec({

    val appGroupRepository = mockk<AppGroupRepository>()
    val jobInfoRepository = mockk<JobInfoRepository>()
    val jobInfoAssembler = mockk<JobInfoAssembler>()
    val namespaceRepository = mockk<NamespaceRepository>()

    val jobInfoService = JobInfoService(
        namespaceRepository = namespaceRepository,
        appGroupRepository = appGroupRepository,
        jobInfoRepository = jobInfoRepository,
        jobInfoAssembler = jobInfoAssembler,
    )

    context("JobInfoService#query") {
        test("should return correct PageDTO") {
            val jobInfoQueryRequestDTO = JobInfoQueryRequestDTO()
            val jobInfo1 = JobInfo()
            val jobInfo2 = JobInfo()
            val jobInfoQueryResponseDTO1 = JobInfoQueryResponseDTO()
            val jobInfoQueryResponseDTO2 = JobInfoQueryResponseDTO()
            val jobInfoQuery = JobInfoQuery()
            val mockPage = Page(
                number = 1,
                size = 10,
                totalElements = 2,
                totalPages = 1,
                content = listOf(jobInfo1, jobInfo2),
            )
            every { jobInfoAssembler.toDomainQuery(any()) } returns  jobInfoQuery
            every { jobInfoRepository.pageQuery(jobInfoQuery) } returns mockPage
            every { jobInfoAssembler.toJobInfoQueryResponseDTO(jobInfo1) } returns jobInfoQueryResponseDTO1
            every { jobInfoAssembler.toJobInfoQueryResponseDTO(jobInfo2) } returns jobInfoQueryResponseDTO2
            val result = jobInfoService.query(jobInfoQueryRequestDTO)
            result.content shouldBeEqual listOf(jobInfoQueryResponseDTO1, jobInfoQueryResponseDTO2)
        }
    }

    context("JobInfoService#detail") {
        test("should return null when jobInfo not exist") {
            every { jobInfoRepository.findById(any()) } returns null
            val result = jobInfoService.detail(1)
            result.shouldBeNull()
        }

        test("should return DTO when jobInfo exist") {
            val jobInfo = JobInfo()
            val jobInfoDetailResponseDTO = JobInfoDetailResponseDTO()
            every { jobInfoRepository.findById(any()) } returns jobInfo
            every { jobInfoAssembler.toJobInfoDetailResponseDTO(jobInfo) } returns jobInfoDetailResponseDTO
            val result = jobInfoService.detail(1)
            result shouldBe jobInfoDetailResponseDTO
        }
    }

})
