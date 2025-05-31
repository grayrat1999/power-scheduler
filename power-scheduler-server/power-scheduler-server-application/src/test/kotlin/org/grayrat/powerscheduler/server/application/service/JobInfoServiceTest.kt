package org.grayrat.powerscheduler.server.application.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.grayrat.powerscheduler.server.application.assembler.JobInfoAssembler
import org.grayrat.powerscheduler.server.application.dto.request.JobInfoQueryRequestDTO
import org.grayrat.powerscheduler.server.application.dto.response.JobInfoDetailResponseDTO
import org.grayrat.powerscheduler.server.application.dto.response.JobInfoQueryResponseDTO
import org.grayrat.powerscheduler.server.domain.appgroup.AppGroupRepository
import org.grayrat.powerscheduler.server.domain.common.Page
import org.grayrat.powerscheduler.server.domain.jobinfo.JobInfo
import org.grayrat.powerscheduler.server.domain.jobinfo.JobInfoRepository

/**
 * @author grayrat
 * @since 2025/4/19
 */
class JobInfoServiceTest : FunSpec({

    val appGroupRepository = mockk<AppGroupRepository>()
    val jobInfoRepository = mockk<JobInfoRepository>()
    val jobInfoAssembler = mockk<JobInfoAssembler>()

    val jobInfoService = JobInfoService(
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

            val mockPage = Page(
                number = 1,
                size = 10,
                totalElements = 2,
                totalPages = 1,
                content = listOf(jobInfo1, jobInfo2),
            )
            every { jobInfoRepository.pageQuery(any()) } returns mockPage
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
