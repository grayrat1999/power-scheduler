package tech.powerscheduler.server.infrastructure.utils

import tech.powerscheduler.server.domain.appgroup.AppGroup
import tech.powerscheduler.server.domain.appgroup.AppGroupId
import tech.powerscheduler.server.domain.common.Page
import tech.powerscheduler.server.domain.jobinfo.JobId
import tech.powerscheduler.server.domain.jobinfo.JobInfo
import tech.powerscheduler.server.domain.jobinstance.JobInstance
import tech.powerscheduler.server.domain.jobinstance.JobInstanceId
import tech.powerscheduler.server.domain.workerregistry.WorkerRegistry
import tech.powerscheduler.server.domain.workerregistry.WorkerRegistryId
import tech.powerscheduler.server.infrastructure.persistence.model.AppGroupEntity
import tech.powerscheduler.server.infrastructure.persistence.model.JobInfoEntity
import tech.powerscheduler.server.infrastructure.persistence.model.JobInstanceEntity
import tech.powerscheduler.server.infrastructure.persistence.model.WorkerRegistryEntity

/**
 * @author grayrat
 * @since 2025/4/17
 */
fun <T> org.springframework.data.domain.Page<T>.toDomainPage(): Page<T> {
    return Page(
        number = this.number + 1,
        size = this.size,
        totalPages = this.totalPages,
        totalElements = this.totalElements,
        content = this.content,
    )
}

fun AppGroupEntity.toDomainModel(): AppGroup {
    return AppGroup().also {
        it.id = AppGroupId(this.id!!)
        it.code = this.code
        it.name = this.name
        it.secret = this.secret
        it.createdAt = this.createdAt
        it.createdBy = this.createdBy
        it.updatedAt = this.updatedAt
        it.updatedBy = this.updatedBy
    }
}

fun AppGroup.toEntity(): AppGroupEntity {
    return AppGroupEntity().also {
        it.id = this.id?.value
        it.code = this.code
        it.name = this.name
        it.secret = this.secret
    }
}

fun JobInfoEntity.toDomainModel(): JobInfo {
    return JobInfo().also {
        it.appGroup = this.appGroupEntity!!.toDomainModel()
        it.id = JobId(this.id!!)
        it.appCode = this.appCode
        it.jobName = this.jobName
        it.jobDesc = this.jobDesc
        it.jobType = this.jobType
        it.scheduleType = this.scheduleType
        it.scheduleConfig = this.scheduleConfig
        it.processor = this.processor
        it.executeMode = this.executeMode
        it.executeParams = this.executeParams
        it.nextScheduleAt = this.nextScheduleAt
        it.enabled = this.enabled
        it.maxConcurrentNum = this.maxConcurrentNum
        it.scheduleType = this.scheduleType
        it.scriptType = this.scriptType
        it.scriptCode = this.scriptCode
        it.maxAttemptCnt = this.maxAttemptCnt
        it.attemptInterval = this.attemptInterval
        it.priority = this.priority
        it.lastCompletedAt = this.lastCompletedAt
        it.schedulerAddress = this.schedulerAddress
        it.retentionPolicy = this.retentionPolicy
        it.retentionValue = this.retentionValue

        it.createdBy = this.createdBy
        it.createdAt = this.createdAt
        it.updatedBy = this.updatedBy
        it.updatedAt = this.updatedAt
    }
}

fun JobInfo.toEntity(): JobInfoEntity {
    return JobInfoEntity().also {
        it.appGroupEntity = this.appGroup?.toEntity()
        it.id = this.id?.value
        it.appCode = this.appCode
        it.jobName = this.jobName
        it.jobDesc = this.jobDesc
        it.jobType = this.jobType
        it.scheduleType = this.scheduleType
        it.scheduleConfig = this.scheduleConfig
        it.processor = this.processor
        it.executeMode = this.executeMode
        it.executeParams = this.executeParams
        it.nextScheduleAt = this.nextScheduleAt
        it.enabled = this.enabled
        it.maxConcurrentNum = this.maxConcurrentNum
        it.scheduleType = this.scheduleType
        it.scriptType = this.scriptType
        it.scriptCode = this.scriptCode
        it.maxAttemptCnt = this.maxAttemptCnt
        it.attemptInterval = this.attemptInterval
        it.priority = this.priority
        it.lastCompletedAt = this.lastCompletedAt
        it.schedulerAddress = this.schedulerAddress
        it.retentionPolicy = this.retentionPolicy
        it.retentionValue = this.retentionValue
    }
}

fun JobInstance.toEntity(): JobInstanceEntity {
    return JobInstanceEntity().also {
        it.appGroupEntity = this.appGroup?.toEntity()
        it.id = this.id?.value
        it.jobId = this.jobId?.value
        it.appCode = this.appCode
        it.schedulerAddress = this.schedulerAddress
        it.workerAddress = this.workerAddress
        it.jobName = this.jobName
        it.jobType = this.jobType
        it.processor = this.processor
        it.jobStatus = this.jobStatus
        it.scheduleAt = this.scheduleAt
        it.startAt = this.startAt
        it.endAt = this.endAt
        it.executeParams = this.executeParams
        it.executeMode = this.executeMode
        it.scheduleType = this.scheduleType
        it.message = this.message
        it.dataTime = this.dataTime
        it.scriptType = this.scriptType
        it.scriptCode = this.scriptCode
        it.attemptCnt = this.attemptCnt
        it.maxAttemptCnt = this.maxAttemptCnt
        it.priority = this.priority
    }
}

fun JobInstanceEntity.toDomainModel(): JobInstance {
    return JobInstance().also {
        it.appGroup = this.appGroupEntity?.toDomainModel()
        it.id = JobInstanceId(this.id!!)
        it.jobId = JobId(this.jobId!!)
        it.appCode = this.appCode
        it.schedulerAddress = this.schedulerAddress
        it.workerAddress = this.workerAddress
        it.jobName = this.jobName
        it.jobType = this.jobType
        it.processor = this.processor
        it.jobStatus = this.jobStatus
        it.scheduleAt = this.scheduleAt
        it.startAt = this.startAt
        it.endAt = this.endAt
        it.executeParams = this.executeParams
        it.executeMode = this.executeMode
        it.scheduleType = this.scheduleType
        it.message = this.message
        it.dataTime = this.dataTime
        it.scriptType = this.scriptType
        it.scriptCode = this.scriptCode
        it.attemptCnt = this.attemptCnt
        it.maxAttemptCnt = this.maxAttemptCnt
        it.priority = this.priority
    }
}

fun WorkerRegistry.toEntity(): WorkerRegistryEntity {
    return WorkerRegistryEntity().also {
        it.id = this.id?.value
        it.appCode = this.appCode
        it.host = this.host
        it.port = this.port
        it.lastHeartbeatAt = this.lastHeartbeatAt
        it.accessToken = this.accessToken
        it.version = this.version
    }
}

fun WorkerRegistryEntity.toDomainModel(): WorkerRegistry {
    return WorkerRegistry().also {
        it.id = WorkerRegistryId(this.id!!)
        it.appCode = this.appCode
        it.host = this.host
        it.port = this.port
        it.accessToken = this.accessToken
        it.lastHeartbeatAt = this.lastHeartbeatAt
        it.version = this.version
    }
}