package org.grayrat.powerscheduler.server.infrastructure.persistence.repository

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import org.grayrat.powerscheduler.common.enums.*
import org.grayrat.powerscheduler.server.domain.common.PageQuery
import org.grayrat.powerscheduler.server.domain.jobinfo.JobId
import org.grayrat.powerscheduler.server.domain.jobinstance.JobInstanceId
import org.grayrat.powerscheduler.server.infrastructure.Bootstrap
import org.grayrat.powerscheduler.server.infrastructure.persistence.model.AppGroupEntity
import org.grayrat.powerscheduler.server.infrastructure.persistence.model.JobInstanceEntity
import org.grayrat.powerscheduler.server.infrastructure.persistence.repository.impl.AppGroupJpaRepository
import org.grayrat.powerscheduler.server.infrastructure.persistence.repository.impl.JobInstanceJpaRepository
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * @author grayrat
 * @since 2025/4/28
 */
@Transactional
@SpringBootTest(classes = [Bootstrap::class])
class JobInstanceRepositoryImplTest(
    val appGroupJpaRepository: AppGroupJpaRepository,
    val jobInstanceJpaRepository: JobInstanceJpaRepository,
    val jobInstanceRepositoryImpl: JobInstanceRepositoryImpl,
    val entityManager: EntityManager,
) : FunSpec({

    context("test ${JobInstanceRepositoryImpl::countByJobIdAndJobStatus}") {
        test("count correctly") {
            val appGroupEntity = AppGroupEntity().also {
                it.code = "code"
                it.name = "name"
                it.secret = "secret"
            }
            appGroupJpaRepository.save(appGroupEntity)
            val jobInstanceEntitiesToSave = generateSequence(0) { it + 1 }
                .take(20)
                .map {
                    JobInstanceEntity().also {
                        it.appGroupEntity = appGroupEntity
                        it.jobId = (1L..10L).random()
                        it.appCode = "appCode"
                        it.schedulerAddress = "schedulerIp"
                        it.workerAddress = "workerAddress"
                        it.jobName = "jobName"
                        it.jobType = JobTypeEnum.entries.random()
                        it.processor = "processor"
                        it.jobStatus = JobStatusEnum.entries.random()
                        it.scheduleAt = LocalDateTime.now()
                        it.endAt = null
                        it.executeParams = "executeParams"
                        it.executeMode = ExecuteModeEnum.entries.random()
                        it.scheduleType = ScheduleTypeEnum.entries.random()
                        it.message = "message"
                        it.dataTime = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS)
                        it.scriptType = ScriptTypeEnum.entries.random()
                        it.scriptCode = "scriptCode"
                        it.maxAttemptCnt = 0
                        it.attemptCnt = 0
                    }
                }
                .toList()
            jobInstanceJpaRepository.saveAll(jobInstanceEntitiesToSave)

            val queryJobStatuses = JobStatusEnum.entries.shuffled().take(2)
            val result = shouldNotThrowAny {
                jobInstanceRepositoryImpl.countByJobIdAndJobStatus(
                    jobIds = jobInstanceEntitiesToSave.map { JobId(it.jobId!!) },
                    jobStatuses = queryJobStatuses,
                )
            }
            val jobId2Count = jobInstanceEntitiesToSave.filter { it.jobStatus in queryJobStatuses }
                .groupingBy { it.jobId!! }
                .eachCount()

            result.entries.shouldForAll {
                val count = jobId2Count[it.key.value]
                it.value shouldBe count
            }
        }
    }


    context("test ${JobInstanceRepositoryImpl::findById}") {
        test("return entity when id exist") {
            val appGroupEntity = AppGroupEntity().also {
                it.code = "code"
                it.name = "name"
                it.secret = "secret"
            }
            appGroupJpaRepository.save(appGroupEntity)
            val entityToSave = JobInstanceEntity().also {
                it.appGroupEntity = appGroupEntity
                it.jobId = (1L..10L).random()
                it.appCode = "appCode"
                it.schedulerAddress = "schedulerIp"
                it.workerAddress = "workerAddress"
                it.jobName = "jobName"
                it.jobType = JobTypeEnum.entries.random()
                it.processor = "processor"
                it.jobStatus = JobStatusEnum.entries.random()
                it.scheduleAt = LocalDateTime.now()
                it.endAt = null
                it.executeParams = "executeParams"
                it.executeMode = ExecuteModeEnum.entries.random()
                it.scheduleType = ScheduleTypeEnum.entries.random()
                it.message = "message"
                it.dataTime = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS)
                it.scriptType = ScriptTypeEnum.entries.random()
                it.scriptCode = "scriptCode"
                it.maxAttemptCnt = 0
                it.attemptCnt = 0
            }
            jobInstanceJpaRepository.save(entityToSave)
            entityManager.clear()
            val result = jobInstanceRepositoryImpl.findById(JobInstanceId(entityToSave.id!!))
            result.shouldNotBeNull()
            result.id!!.value shouldBe entityToSave.id
        }
    }

    context("test ${JobInstanceRepositoryImpl::listDispatchable}") {
        test("return entity when id exist") {
            jobInstanceRepositoryImpl.listDispatchable(
                jobIds = listOf(1L, 2L, 3L).map { JobId(it) },
                pageQuery = PageQuery(1, 20),
            )
        }
    }

    context("test ${JobInstanceRepositoryImpl::findAllUncompletedByWorkerAddress}") {
        test("return entity when id exist") {
            val appGroupEntity = AppGroupEntity().also {
                it.code = "code"
                it.name = "name"
                it.secret = "secret"
            }
            appGroupJpaRepository.save(appGroupEntity)
            val entityToSave = JobInstanceEntity().also {
                it.appGroupEntity = appGroupEntity
                it.jobId = (1L..10L).random()
                it.appCode = "appCode"
                it.schedulerAddress = "schedulerIp"
                it.workerAddress = "workerAddress"
                it.jobName = "jobName"
                it.jobType = JobTypeEnum.entries.random()
                it.processor = "processor"
                it.jobStatus = JobStatusEnum.WAITING_DISPATCH
                it.scheduleAt = LocalDateTime.now()
                it.endAt = null
                it.executeParams = "executeParams"
                it.executeMode = ExecuteModeEnum.entries.random()
                it.scheduleType = ScheduleTypeEnum.entries.random()
                it.message = "message"
                it.dataTime = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS)
                it.scriptType = ScriptTypeEnum.entries.random()
                it.scriptCode = "scriptCode"
                it.maxAttemptCnt = 0
                it.attemptCnt = 0
                it.workerAddress = "workerAddress"
            }
            jobInstanceJpaRepository.save(entityToSave)
            entityManager.clear()
            jobInstanceRepositoryImpl.findAllUncompletedByWorkerAddress(
                workerAddress = "workerAddress",
            )
        }
    }

})
