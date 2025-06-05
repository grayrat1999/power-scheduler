package tech.powerscheduler.server.application.assembler

import org.springframework.stereotype.Component
import tech.powerscheduler.common.enums.JobTypeEnum.JAVA
import tech.powerscheduler.common.enums.JobTypeEnum.SCRIPT
import tech.powerscheduler.common.enums.ScheduleTypeEnum
import tech.powerscheduler.server.application.dto.request.JobInfoAddRequestDTO
import tech.powerscheduler.server.application.dto.request.JobInfoEditRequestDTO
import tech.powerscheduler.server.application.dto.response.JobInfoDetailResponseDTO
import tech.powerscheduler.server.application.dto.response.JobInfoQueryResponseDTO
import tech.powerscheduler.server.application.utils.toDTO
import tech.powerscheduler.server.domain.appgroup.AppGroup
import tech.powerscheduler.server.domain.jobinfo.JobInfo

/**
 * @author grayrat
 * @since 2025/4/18
 */
@Component
class JobInfoAssembler {

    fun toJobInfoQueryResponseDTO(jobInfo: JobInfo): JobInfoQueryResponseDTO {
        return JobInfoQueryResponseDTO().apply {
            this.id = jobInfo.id!!.value
            this.appCode = jobInfo.appGroup?.code
            this.appName = jobInfo.appGroup?.name
            this.jobName = jobInfo.jobName
            this.jobDesc = jobInfo.jobDesc
            this.jobType = jobInfo.jobType.toDTO()
            this.scheduleType = jobInfo.scheduleType.toDTO()
            this.scheduleConfig = jobInfo.scheduleConfig
            this.processor = jobInfo.processor
            this.executeMode = jobInfo.executeMode.toDTO()
            this.executeParams = jobInfo.executeParams
            this.nextScheduleAt = jobInfo.nextScheduleAt
            this.enabled = jobInfo.enabled
            this.maxConcurrentNum = jobInfo.maxConcurrentNum
            this.scriptType = jobInfo.scriptType.toDTO()
            this.scriptCode = jobInfo.scriptCode
            this.maxAttemptCnt = jobInfo.maxAttemptCnt
            this.priority = jobInfo.priority
            this.createdBy = jobInfo.createdBy
            this.createdAt = jobInfo.createdAt
            this.updatedBy = jobInfo.updatedBy
            this.updatedAt = jobInfo.updatedAt

            val scheduleType = jobInfo.scheduleType!!
            this.scheduleConfigDesc = when (scheduleType) {
                ScheduleTypeEnum.CRON -> scheduleConfig
                ScheduleTypeEnum.FIX_RATE -> "${scheduleType.label} | $scheduleConfig(秒)"
                ScheduleTypeEnum.FIX_DELAY -> "${scheduleType.label} | $scheduleConfig(秒)"
                ScheduleTypeEnum.ONE_TIME -> scheduleConfig
            }
        }
    }

    fun toJobInfoDetailResponseDTO(jobInfo: JobInfo): JobInfoDetailResponseDTO {
        return JobInfoDetailResponseDTO().apply {
            this.id = jobInfo.id!!.value
            this.appCode = jobInfo.appCode
            this.jobName = jobInfo.jobName
            this.jobDesc = jobInfo.jobDesc
            this.jobType = jobInfo.jobType.toDTO()
            this.scheduleType = jobInfo.scheduleType.toDTO()
            this.scheduleConfig = jobInfo.scheduleConfig
            this.processor = jobInfo.processor
            this.executeMode = jobInfo.executeMode.toDTO()
            this.executeParams = jobInfo.executeParams
            this.nextScheduleAt = jobInfo.nextScheduleAt
            this.enabled = jobInfo.enabled
            this.maxConcurrentNum = jobInfo.maxConcurrentNum
            this.scriptType = jobInfo.scriptType.toDTO()
            this.scriptCode = jobInfo.scriptCode
            this.maxAttemptCnt = jobInfo.maxAttemptCnt
            this.attemptInterval = jobInfo.attemptInterval
            this.priority = jobInfo.priority
            this.retentionPolicy = jobInfo.retentionPolicy.toDTO()
            this.retentionValue = jobInfo.retentionValue
            this.createdBy = jobInfo.createdBy
            this.createdAt = jobInfo.createdAt
            this.updatedBy = jobInfo.updatedBy
            this.updatedAt = jobInfo.updatedAt
        }
    }

    fun toDomainModel4AddRequest(param: JobInfoAddRequestDTO, appGroup: AppGroup): JobInfo {
        return JobInfo().apply {
            this.appGroup = appGroup
            this.appCode = param.appCode
            this.jobName = param.jobName
            this.jobDesc = param.jobDesc
            this.jobType = param.jobType
            this.scheduleType = param.scheduleType
            this.scheduleConfig = param.scheduleConfig
            this.processor = when (param.jobType!!) {
                JAVA -> param.processor
                SCRIPT -> "ScriptProcessor"
            }
            this.executeMode = param.executeMode
            this.executeParams = param.executeParams
            this.nextScheduleAt = null
            this.enabled = false
            this.maxConcurrentNum = if (scheduleType == ScheduleTypeEnum.FIX_DELAY) 1 else param.maxConcurrentNum
            this.attemptInterval = param.attemptInterval
            this.scriptType = param.scriptType
            this.scriptCode = param.scriptCode
            this.maxAttemptCnt = param.maxAttemptCnt
            this.priority = param.priority
            this.retentionPolicy = param.retentionPolicy
            this.retentionValue = param.retentionValue
        }
    }

    fun toDomainModel4EditRequest(jobInfo: JobInfo, param: JobInfoEditRequestDTO): JobInfo {
        return JobInfo().apply {
            this.id = jobInfo.id
            this.appGroup = jobInfo.appGroup
            this.appCode = jobInfo.appCode
            this.jobName = param.jobName
            this.jobDesc = param.jobDesc
            this.jobType = param.jobType
            this.scheduleType = param.scheduleType
            this.scheduleConfig = param.scheduleConfig
            this.processor = when (param.jobType!!) {
                JAVA -> param.processor
                SCRIPT -> "ScriptProcessor"
            }
            this.executeMode = param.executeMode
            this.executeParams = param.executeParams
            this.enabled = false
            this.maxConcurrentNum = if (scheduleType == ScheduleTypeEnum.FIX_DELAY) 1 else param.maxConcurrentNum
            this.attemptInterval = param.attemptInterval
            this.scriptType = param.scriptType
            this.scriptCode = param.scriptCode
            this.maxAttemptCnt = param.maxAttemptCnt
            this.priority = param.priority
            this.schedulerAddress = null
            this.retentionPolicy = param.retentionPolicy
            this.retentionValue = param.retentionValue
        }
    }

}