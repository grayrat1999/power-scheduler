package tech.powerscheduler.server.application.service

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.support.TransactionTemplate
import tech.powerscheduler.common.exception.BizException
import tech.powerscheduler.server.application.assembler.JobInstanceAssembler
import tech.powerscheduler.server.application.assembler.TaskAssembler
import tech.powerscheduler.server.application.dto.request.JobInstanceQueryRequestDTO
import tech.powerscheduler.server.application.dto.request.JobRunRequestDTO
import tech.powerscheduler.server.application.dto.response.JobInstanceDetailResponseDTO
import tech.powerscheduler.server.application.dto.response.JobInstanceQueryResponseDTO
import tech.powerscheduler.server.domain.common.Page
import tech.powerscheduler.server.domain.jobinfo.JobId
import tech.powerscheduler.server.domain.jobinfo.JobInfo
import tech.powerscheduler.server.domain.jobinfo.JobInfoRepository
import tech.powerscheduler.server.domain.jobinstance.JobInstance
import tech.powerscheduler.server.domain.jobinstance.JobInstanceId
import tech.powerscheduler.server.domain.jobinstance.JobInstanceQuery
import tech.powerscheduler.server.domain.jobinstance.JobInstanceRepository
import tech.powerscheduler.server.domain.task.TaskRepository

class JobInstanceServiceTest : FunSpec({

    val taskRepository = mockk<TaskRepository>()
    val jobInfoRepository = mockk<JobInfoRepository>()
    val jobInstanceRepository = mockk<JobInstanceRepository>()
    val taskAssembler = mockk<TaskAssembler>()
    val jobInstanceAssembler = mockk<JobInstanceAssembler>()
    val transactionTemplate = mockk<TransactionTemplate>()
    val applicationEventPublisher = mockk<ApplicationEventPublisher>()

    val jobInstanceService = JobInstanceService(
        taskRepository = taskRepository,
        jobInfoRepository = jobInfoRepository,
        jobInstanceRepository = jobInstanceRepository,
        jobInstanceAssembler = jobInstanceAssembler,
        transactionTemplate = transactionTemplate,
        applicationEventPublisher = applicationEventPublisher,
        taskAssembler = taskAssembler,
    )

    context("test ${JobInstanceService::list}") {
        test("success") {
            val param = JobInstanceQueryRequestDTO()
            val jobInstanceQuery = JobInstanceQuery()
            val page = Page<JobInstance>()
            every { transactionTemplate.executeWithoutResult { any() } } just Runs
            every { jobInstanceAssembler.toDomainQuery(param) } returns jobInstanceQuery
            every { jobInstanceRepository.pageQuery(jobInstanceQuery) } returns page
            every { jobInstanceAssembler.toJobInstanceQueryResponseDTO(any()) } returns JobInstanceQueryResponseDTO()
            shouldNotThrowAny { jobInstanceService.list(param) }
        }
    }


    context("test ${JobInstanceService::detail}") {
        test("return null when jobInstanceId not exists") {
            every { jobInstanceRepository.findById(any()) } returns null
            val result = shouldNotThrowAny { jobInstanceService.detail(1L) }
            result.shouldBeNull()
        }

        test("return DTO when jobInstanceId exists") {
            val jobInstance = JobInstance().also {
                it.id = JobInstanceId(1L)
            }
            val jobInstanceDetailResponseDTO = JobInstanceDetailResponseDTO().also {
                it.id = jobInstance.id!!.value
            }
            every { jobInstanceRepository.findById(jobInstance.id!!) } returns jobInstance
            every { jobInstanceAssembler.toJobInstanceQueryDetailDTO(jobInstance) } returns jobInstanceDetailResponseDTO
            val result = shouldNotThrowAny { jobInstanceService.detail(1L) }
            result.shouldNotBeNull()
            result shouldBe jobInstanceDetailResponseDTO
        }
    }

    context("test ${JobInstanceService::run}") {
        test("throw BizException when jobId not exists") {
            val param = JobRunRequestDTO().also {
                it.jobId = 1L
            }
            every { jobInfoRepository.findById(any()) } returns null
            shouldThrow<BizException> {
                jobInstanceService.run(param)
            }
        }

        test("return id when jobId exists") {
            val param = JobRunRequestDTO().also {
                it.jobId = 1L
            }
            val jobInfo = spyk(JobInfo()).also {
                it.id = JobId(param.jobId!!)
            }
            val jobInstanceId = JobInstanceId(1L)
            val jobInstance = JobInstance().also {
                it.id = jobInstanceId
                it.jobId = JobId(param.jobId!!)
            }
            every { jobInfoRepository.findById(JobId(param.jobId!!)) } returns jobInfo
            every { jobInfo.createInstance() } returns jobInstance
            every { jobInstanceRepository.save(jobInstance) } returns jobInstanceId
            val result = shouldNotThrowAny { jobInstanceService.run(param) }
            result.shouldNotBeNull()
            result shouldBe jobInstanceId.value
        }
    }

    context("test ${JobInstanceService::reattempt}") {
        test("throw BizException when jobInstanceId not exists") {
            every { jobInstanceRepository.findById(any()) } returns null
            shouldThrow<BizException> {
                jobInstanceService.reattempt(1L)
            }
        }

        test("return id when jobInstanceId exists") {
            val param = 1L
            val jobInstance = spyk(JobInstance()).also {
                it.id = JobInstanceId(param)
            }
            val jobInstanceToReattempt = JobInstance()
            val reattemptJobInstanceId = JobInstanceId(2L)
            every { jobInstanceRepository.findById(JobInstanceId(param)) } returns jobInstance
            every { jobInstance.cloneForReattempt() } returns jobInstanceToReattempt
            every { jobInstanceRepository.save(jobInstanceToReattempt) } returns reattemptJobInstanceId
            val result = shouldNotThrowAny { jobInstanceService.reattempt(param) }
            result.shouldNotBeNull()
            result shouldBe reattemptJobInstanceId.value
        }
    }
})
