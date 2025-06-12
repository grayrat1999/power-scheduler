package tech.powerscheduler.server.application.assembler

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import tech.powerscheduler.common.enums.ExecuteModeEnum
import tech.powerscheduler.common.enums.JobTypeEnum
import tech.powerscheduler.common.enums.ScheduleTypeEnum
import tech.powerscheduler.common.enums.ScriptTypeEnum
import tech.powerscheduler.server.application.dto.request.JobInfoAddRequestDTO
import tech.powerscheduler.server.application.dto.request.JobInfoEditRequestDTO
import tech.powerscheduler.server.domain.appgroup.AppGroup
import tech.powerscheduler.server.domain.jobinfo.JobId
import tech.powerscheduler.server.domain.jobinfo.JobInfo
import java.time.LocalDateTime

class JobInfoAssemblerTest : FunSpec({

    val jobInfoAssembler = JobInfoAssembler()

    context("test ${JobInfoAssembler::toJobInfoQueryResponseDTO.name}") {
        test("should assemble correct DTO") {
            val appGroup = AppGroup().also {
                it.code = "appCode"
                it.name = "appGroupName"
            }
            val jobInfo = JobInfo().also {
                it.id = JobId(1)
                it.appGroup = appGroup
                it.appCode = "appCode"
                it.jobName = "jobName"
                it.jobDesc = "jobDesc"
                it.jobType = JobTypeEnum.JAVA
                it.scheduleType = ScheduleTypeEnum.FIX_RATE
                it.scheduleConfig = "3"
                it.processor = "processor"
                it.executeMode = ExecuteModeEnum.SINGLE
                it.executeParams = "executeParams"
                it.nextScheduleAt = LocalDateTime.now()
                it.enabled = true
                it.maxConcurrentNum = 1
                it.scriptType = ScriptTypeEnum.PYTHON
                it.scriptCode = "scriptCode"
            }
            val result = shouldNotThrowAny {
                jobInfoAssembler.toJobInfoQueryResponseDTO(jobInfo)
            }
            result.id shouldBe jobInfo.id?.value
            result.appCode shouldBe jobInfo.appCode
            result.jobName shouldBe jobInfo.jobName
            result.jobDesc shouldBe jobInfo.jobDesc
            result.jobType!!.code shouldBe jobInfo.jobType?.code
            result.jobType!!.label shouldBe jobInfo.jobType?.label
            result.scheduleType!!.code shouldBe jobInfo.scheduleType?.code
            result.scheduleType!!.label shouldBe jobInfo.scheduleType?.label
            result.scheduleConfig shouldBe jobInfo.scheduleConfig
            result.processor shouldBe jobInfo.processor
            result.executeMode!!.code shouldBe jobInfo.executeMode?.code
            result.executeMode!!.label shouldBe jobInfo.executeMode?.label
            result.nextScheduleAt shouldBe jobInfo.nextScheduleAt
            result.enabled shouldBe jobInfo.enabled
            result.scriptType!!.code shouldBe jobInfo.scriptType?.code
            result.scriptType!!.label shouldBe jobInfo.scriptType?.label
        }
    }

    context("test ${JobInfoAssembler::toDomainModel4AddRequest.name}") {
        test("should assemble correct domain model") {
            val appGroup = AppGroup().also {
                it.code = "code"
                it.name = "name"
            }
            val jobInfoAddRequestDTO = JobInfoAddRequestDTO().also {
                it.appCode = "appCode"
                it.jobName = "jobName"
                it.jobDesc = "jobDesc"
                it.scheduleType = ScheduleTypeEnum.FIX_RATE
                it.scheduleConfig = "scheduleConfig"
                it.jobType = JobTypeEnum.JAVA
                it.processor = "processor"
                it.executeMode = ExecuteModeEnum.SINGLE
                it.executeParams = "executeParams"
                it.maxConcurrentNum = 1
                it.scriptType = ScriptTypeEnum.BASH
                it.scriptCode = "scriptCode"
            }
            val result = shouldNotThrowAny {
                jobInfoAssembler.toDomainModel4AddRequest(
                    param = jobInfoAddRequestDTO,
                    appGroup = appGroup,
                )
            }
            result.id.shouldBeNull()
            result.appGroup shouldBe appGroup
            result.appCode shouldBe jobInfoAddRequestDTO.appCode
            result.jobName shouldBe jobInfoAddRequestDTO.jobName
            result.jobDesc shouldBe jobInfoAddRequestDTO.jobDesc
            result.jobType shouldBe jobInfoAddRequestDTO.jobType
            result.scheduleType shouldBe jobInfoAddRequestDTO.scheduleType
            result.scheduleConfig shouldBe jobInfoAddRequestDTO.scheduleConfig
            result.processor shouldBe jobInfoAddRequestDTO.processor
            result.executeMode shouldBe jobInfoAddRequestDTO.executeMode
            result.executeParams shouldBe jobInfoAddRequestDTO.executeParams
            result.nextScheduleAt.shouldBeNull()
            result.enabled shouldBe false
            result.maxConcurrentNum shouldBe jobInfoAddRequestDTO.maxConcurrentNum
            result.scriptType shouldBe jobInfoAddRequestDTO.scriptType
            result.scriptCode shouldBe jobInfoAddRequestDTO.scriptCode
        }
    }

    context("test ${JobInfoAssembler::toDomainModel4EditRequest.name}") {
        test("should assemble correct domain model") {
            val appGroup = AppGroup().also {
                it.code = "code"
                it.name = "name"
            }
            val jobInfoToEdit = JobInfo().also {
                it.appGroup = appGroup
                it.id = JobId(1L)
                it.appCode = "old-appCode"
                it.jobName = "old-jobName"
                it.jobDesc = "old-jobDesc"
                it.jobType = JobTypeEnum.SCRIPT
                it.scheduleType = ScheduleTypeEnum.ONE_TIME
                it.scheduleConfig = "old-scheduleConfig"
                it.processor = "old-processor"
                it.executeMode = ExecuteModeEnum.entries.random()
                it.executeParams = "old-executeParams"
                it.nextScheduleAt = LocalDateTime.now().plusDays(1)
                it.enabled = true
                it.maxConcurrentNum = 1
                it.scriptType = ScriptTypeEnum.PYTHON
                it.scriptCode = "old-scriptCode"
            }
            val jobInfoEditRequestDTO = JobInfoEditRequestDTO().also {
                it.jobId = 1L
                it.jobName = "jobName"
                it.jobDesc = "jobDesc"
                it.scheduleType = (ScheduleTypeEnum.entries.toList() - jobInfoToEdit.scheduleType).random()
                it.scheduleConfig = "scheduleConfig"
                it.jobType = (JobTypeEnum.entries.toList() - jobInfoToEdit.jobType).random()
                it.processor = "processor"
                it.executeMode = (ExecuteModeEnum.entries.toList() - jobInfoToEdit.executeMode).random()
                it.executeParams = "executeParams"
                it.maxConcurrentNum = 1
                it.scriptType = (ScriptTypeEnum.entries.toList() - jobInfoToEdit.scriptType).random()
                it.scriptCode = "scriptCode"
            }
            val result = shouldNotThrowAny {
                jobInfoAssembler.toDomainModel4EditRequest(
                    jobInfo = jobInfoToEdit,
                    param = jobInfoEditRequestDTO,
                )
            }
            result.id shouldBe jobInfoToEdit.id
            result.appGroup shouldBe appGroup
            result.appCode shouldBe jobInfoToEdit.appCode
            result.jobName shouldBe jobInfoEditRequestDTO.jobName
            result.jobDesc shouldBe jobInfoEditRequestDTO.jobDesc
            result.jobType shouldBe jobInfoEditRequestDTO.jobType
            result.scheduleType shouldBe jobInfoEditRequestDTO.scheduleType
            result.scheduleConfig shouldBe jobInfoEditRequestDTO.scheduleConfig
            result.processor shouldBe jobInfoEditRequestDTO.processor
            result.executeMode shouldBe jobInfoEditRequestDTO.executeMode
            result.executeParams shouldBe jobInfoEditRequestDTO.executeParams
            result.nextScheduleAt.shouldBeNull()
            result.enabled shouldBe false
            result.maxConcurrentNum shouldBe jobInfoEditRequestDTO.maxConcurrentNum
            result.scriptType shouldBe jobInfoEditRequestDTO.scriptType
            result.scriptCode shouldBe jobInfoEditRequestDTO.scriptCode
        }
    }
})
