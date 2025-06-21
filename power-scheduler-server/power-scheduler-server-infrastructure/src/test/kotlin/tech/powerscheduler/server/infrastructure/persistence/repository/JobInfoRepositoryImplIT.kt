package tech.powerscheduler.server.infrastructure.persistence.repository

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import jakarta.transaction.Transactional
import org.springframework.boot.test.context.SpringBootTest
import tech.powerscheduler.common.enums.ExecuteModeEnum
import tech.powerscheduler.common.enums.JobTypeEnum
import tech.powerscheduler.common.enums.ScheduleTypeEnum
import tech.powerscheduler.common.enums.ScriptTypeEnum
import tech.powerscheduler.server.domain.appgroup.AppGroup
import tech.powerscheduler.server.domain.appgroup.AppGroupId
import tech.powerscheduler.server.domain.job.JobId
import tech.powerscheduler.server.domain.job.JobInfo
import tech.powerscheduler.server.domain.job.JobInfoQuery
import tech.powerscheduler.server.infrastructure.Bootstrap
import tech.powerscheduler.server.infrastructure.persistence.model.AppGroupEntity
import tech.powerscheduler.server.infrastructure.persistence.model.JobInfoEntity
import tech.powerscheduler.server.infrastructure.persistence.model.NamespaceEntity
import tech.powerscheduler.server.infrastructure.persistence.repository.impl.AppGroupJpaRepository
import tech.powerscheduler.server.infrastructure.persistence.repository.impl.JobInfoJpaRepository
import tech.powerscheduler.server.infrastructure.persistence.repository.impl.NamespaceJpaRepository

/**
 * @author grayrat
 * @since 2025/4/20
 */
@Transactional
@SpringBootTest(classes = [Bootstrap::class])
class JobInfoRepositoryImplIT(
    private val namespaceJpaRepository: NamespaceJpaRepository,
    private val appGroupJpaRepository: AppGroupJpaRepository,
    private val jobInfoJpaRepository: JobInfoJpaRepository,
    private val jobInfoRepositoryImpl: JobInfoRepositoryImpl,
) : FunSpec({

    data class Data(
        val namespaceEntities: List<NamespaceEntity>,
        val appGroupEntities: List<AppGroupEntity>,
        val jobInfoEntities: List<JobInfoEntity>,
    )

    context("test ${JobInfoRepositoryImpl::pageQuery.name}") {

        fun prepareData(): Data {
            val namespaceEntity = NamespaceEntity().also {
                it.code = "namespaceCode"
                it.name = "namespaceName"
            }
            val appGroupEntity = AppGroupEntity().also {
                it.namespaceEntity = namespaceEntity
                it.code = "appCode"
                it.name = "appGroupName"
            }
            val jobInfoEntities = generateSequence(1L) { it + 1 }
                .take(30)
                .map {
                    JobInfoEntity().also {
                        it.appGroupEntity = appGroupEntity
                        it.jobName = "jobName_$it"
                        it.jobDesc = "jobDesc_$it"
                        it.jobType = JobTypeEnum.entries.random()
                        it.scheduleType = ScheduleTypeEnum.entries.random()
                        it.scheduleConfig = "scheduleConfig_$it"
                        it.processor = "processor_$it"
                        it.executeMode = ExecuteModeEnum.entries.random()
                        it.executeParams = "executeParams_$it"
                        it.nextScheduleAt = null
                        it.enabled = false
                        it.maxConcurrentNum = 1
                    }
                }
                .toList()
            namespaceJpaRepository.save(namespaceEntity)
            appGroupJpaRepository.save(appGroupEntity)
            jobInfoJpaRepository.saveAll(jobInfoEntities)
            return Data(
                namespaceEntities = listOf(namespaceEntity),
                appGroupEntities = listOf(appGroupEntity),
                jobInfoEntities = jobInfoEntities,
            )
        }

        test("filter by appCode") {
            val (namespaceEntities, appGroupEntities, jobInfoEntities) = prepareData()
            val query = JobInfoQuery().also {
                it.namespaceCode = namespaceEntities.random().code
                it.appCode = jobInfoEntities.random().appGroupEntity?.code
                it.pageSize = jobInfoEntities.size
            }
            val result = jobInfoRepositoryImpl.pageQuery(query)
            val filterEntityIdSet = jobInfoEntities.filter { it.appGroupEntity!!.code == query.appCode }
                .map { it.id }
                .toSet()
            result.totalElements shouldBe filterEntityIdSet.size
            result.content.map { it.id?.value }.toSet() shouldBe filterEntityIdSet
        }

        test("filter by jobName") {
            val (namespaceEntities, appGroupEntities, jobInfoEntities) = prepareData()
            val query = JobInfoQuery().also {
                it.namespaceCode = namespaceEntities.random().code
                it.jobName = jobInfoEntities.random().jobName
                it.pageSize = jobInfoEntities.size
            }
            val result = jobInfoRepositoryImpl.pageQuery(query)
            val filterEntityIdSet = jobInfoEntities.filter { it.jobName == query.jobName }.map { it.id }.toSet()
            result.totalElements shouldBe filterEntityIdSet.size
            result.content.map { it.id?.value }.toSet() shouldBe filterEntityIdSet
        }

        test("filter by processorLike") {
            val (namespaceEntities, appGroupEntities, jobInfoEntities) = prepareData()
            val query = JobInfoQuery().also {
                it.namespaceCode = namespaceEntities.random().code
                it.processor = jobInfoEntities.random().processor
                it.pageSize = jobInfoEntities.size
            }
            val result = jobInfoRepositoryImpl.pageQuery(query)
            val filterEntityIdSet = jobInfoEntities.filter { it.processor == query.processor }.map { it.id }.toSet()
            result.totalElements shouldBe filterEntityIdSet.size
            result.content.map { it.id?.value }.toSet() shouldBe filterEntityIdSet
        }
    }

    context("test ${JobInfoRepositoryImpl::findById.name}") {
        test("should return null when id not exists") {
            val result = shouldNotThrowAny {
                jobInfoRepositoryImpl.findById(JobId(1L))
            }
            result.shouldBeNull()
        }

        test("should return domain model when id exists") {
            val namespaceEntity = NamespaceEntity().also {
                it.code = "namespaceCode"
                it.name = "namespaceName"
            }
            val appGroupEntity = AppGroupEntity().also {
                it.namespaceEntity = namespaceEntity
                it.code = "appCode"
                it.name = "appGroupName"
            }
            val jobInfoEntity = JobInfoEntity().also {
                it.appGroupEntity = appGroupEntity
                it.jobName = "jobName"
                it.jobDesc = "jobDesc"
                it.jobType = JobTypeEnum.entries.random()
                it.scheduleType = ScheduleTypeEnum.entries.random()
                it.scheduleConfig = ""
                it.processor = "processor"
                it.executeMode = ExecuteModeEnum.entries.random()
                it.executeParams = ""
                it.nextScheduleAt = null
                it.enabled = false
                it.maxConcurrentNum = 1
            }
            namespaceJpaRepository.save(namespaceEntity)
            appGroupJpaRepository.save(appGroupEntity)
            jobInfoJpaRepository.save(jobInfoEntity)
            val result = shouldNotThrowAny {
                jobInfoRepositoryImpl.findById(JobId(jobInfoEntity.id!!))
            }
            result.shouldNotBeNull()
        }
    }

    context("test ${JobInfoRepositoryImpl::findAllByIds.name}") {
        test("should return empty list when no id exists") {
            val result = shouldNotThrowAny {
                jobInfoRepositoryImpl.findAllByIds(setOf(JobId(1L)))
            }
            result.shouldBeEmpty()
        }

        test("should return list when any id exists") {
            val namespaceEntity = NamespaceEntity().also {
                it.code = "namespaceCode"
                it.name = "namespaceName"
            }
            val appGroupEntity = AppGroupEntity().also {
                it.namespaceEntity = namespaceEntity
                it.code = "appCode"
                it.name = "appGroupName"
            }
            val jobInfoEntities = generateSequence(1L) { it + 1 }
                .take(2)
                .map {
                    JobInfoEntity().also {
                        it.appGroupEntity = appGroupEntity
                        it.jobName = "jobName"
                        it.jobDesc = "jobDesc"
                        it.jobType = JobTypeEnum.entries.random()
                        it.scheduleType = ScheduleTypeEnum.entries.random()
                        it.scheduleConfig = ""
                        it.processor = "processor"
                        it.executeMode = ExecuteModeEnum.entries.random()
                        it.executeParams = ""
                        it.nextScheduleAt = null
                        it.enabled = false
                        it.maxConcurrentNum = 1
                    }
                }
                .toList()
            namespaceJpaRepository.save(namespaceEntity)
            appGroupJpaRepository.save(appGroupEntity)
            jobInfoJpaRepository.saveAll(jobInfoEntities)
            val result = shouldNotThrowAny {
                jobInfoRepositoryImpl.findAllByIds(jobInfoEntities.map { JobId(it.id!!) })
            }
            result.shouldNotBeEmpty()
            result.size shouldBe jobInfoEntities.size
        }
    }

    context("test ${JobInfoRepositoryImpl::save.name}") {
        test("should succeed when invoke 'insert' operation") {
            val namespaceEntity = NamespaceEntity().also {
                it.code = "namespaceCode"
                it.name = "namespaceName"
            }
            val appGroupEntity = AppGroupEntity().also {
                it.namespaceEntity = namespaceEntity
                it.code = "appCode"
                it.name = "appGroupName"
            }
            namespaceJpaRepository.save(namespaceEntity)
            appGroupJpaRepository.save(appGroupEntity)

            val appGroup = AppGroup().also {
                it.id = AppGroupId(appGroupEntity.id!!)
                it.code = "appCode"
                it.name = "appGroupName"
            }
            val jobInfo = JobInfo().also {
                it.appGroup = appGroup
                it.jobName = "jobName"
                it.jobDesc = "jobDesc"
                it.jobType = JobTypeEnum.entries.random()
                it.scheduleType = ScheduleTypeEnum.entries.random()
                it.scheduleConfig = ""
                it.processor = "processor"
                it.executeMode = ExecuteModeEnum.entries.random()
                it.executeParams = ""
                it.nextScheduleAt = null
                it.enabled = false
                it.maxConcurrentNum = 1
            }
            val result = shouldNotThrowAny {
                jobInfoRepositoryImpl.save(jobInfo)
            }
            result.shouldNotBeNull()
        }

        test("should succeed when invoke 'update' operation") {
            // initialize test data
            val namespaceEntity = NamespaceEntity().also {
                it.code = "namespaceCode"
                it.name = "namespaceName"
            }
            val appGroupEntity = AppGroupEntity().also {
                it.namespaceEntity = namespaceEntity
                it.code = "appCode"
                it.name = "appGroupName"
            }
            val jobInfoEntity = JobInfoEntity().also {
                it.appGroupEntity = appGroupEntity
                it.jobName = "old-jobName"
                it.jobDesc = "old-jobDesc"
                it.jobType = JobTypeEnum.entries.random()
                it.scheduleType = ScheduleTypeEnum.entries.random()
                it.scheduleConfig = "old-scheduleConfig"
                it.processor = "old-processor"
                it.executeMode = ExecuteModeEnum.entries.random()
                it.executeParams = "old-executeParams"
                it.nextScheduleAt = null
                it.enabled = false
                it.maxConcurrentNum = 1
                it.scriptType = ScriptTypeEnum.entries.random()
                it.scriptCode = "old-scriptCode"
            }
            namespaceJpaRepository.save(namespaceEntity)
            appGroupJpaRepository.save(appGroupEntity)
            jobInfoJpaRepository.save(jobInfoEntity)

            val appGroup = AppGroup().also {
                it.id = AppGroupId(appGroupEntity.id!!)
                it.code = "appCode"
                it.name = "appGroupName"
            }
            val jobInfo = JobInfo().also {
                it.id = JobId(jobInfoEntity.id!!)
                it.appGroup = appGroup
                it.jobName = "jobName"
                it.jobDesc = "jobDesc"
                it.jobType = (JobTypeEnum.entries - jobInfoEntity.jobType).random()
                it.scheduleType = (ScheduleTypeEnum.entries - jobInfoEntity.scheduleType).random()
                it.scheduleConfig = "scheduleConfig"
                it.processor = "processor"
                it.executeMode = (ExecuteModeEnum.entries - jobInfoEntity.executeMode).random()
                it.executeParams = "executeParams"
                it.nextScheduleAt = null
                it.enabled = false
                it.maxConcurrentNum = 10
                it.scriptType = (ScriptTypeEnum.entries - jobInfoEntity.scriptType).random()
                it.scriptCode = "scriptCode"
            }
            val result = shouldNotThrowAny {
                jobInfoRepositoryImpl.save(jobInfo)
            }
            result.shouldNotBeNull()
            result shouldBe jobInfo.id
        }
    }

    context("test ${JobInfoRepositoryImpl::deleteById.name}") {
        test("should not throw when id not exists") {
            shouldNotThrowAny {
                jobInfoRepositoryImpl.deleteById(JobId(0L))
            }
        }

        test("should not throw when id exists") {
            // initialize test data
            val namespaceEntity = NamespaceEntity().also {
                it.code = "namespaceCode"
                it.name = "namespaceName"
            }
            val appGroupEntity = AppGroupEntity().also {
                it.namespaceEntity = namespaceEntity
                it.code = "appCode"
                it.name = "appGroupName"
            }
            val jobInfoEntity = JobInfoEntity().also {
                it.appGroupEntity = appGroupEntity
                it.jobName = "old-jobName"
                it.jobDesc = "old-jobDesc"
                it.jobType = JobTypeEnum.entries.random()
                it.scheduleType = ScheduleTypeEnum.entries.random()
                it.scheduleConfig = "old-scheduleConfig"
                it.processor = "old-processor"
                it.executeMode = ExecuteModeEnum.entries.random()
                it.executeParams = "old-executeParams"
                it.nextScheduleAt = null
                it.enabled = false
                it.maxConcurrentNum = 1
                it.scriptType = ScriptTypeEnum.entries.random()
                it.scriptCode = "old-scriptCode"
            }
            namespaceJpaRepository.save(namespaceEntity)
            appGroupJpaRepository.save(appGroupEntity)
            jobInfoJpaRepository.save(jobInfoEntity)

            shouldNotThrowAny {
                jobInfoRepositoryImpl.deleteById(JobId(appGroupEntity.id!!))
            }
        }
    }
})