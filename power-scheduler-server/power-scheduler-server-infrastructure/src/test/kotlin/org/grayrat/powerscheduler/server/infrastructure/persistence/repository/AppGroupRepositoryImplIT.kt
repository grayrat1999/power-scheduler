package org.grayrat.powerscheduler.server.infrastructure.persistence.repository

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.grayrat.powerscheduler.server.domain.appgroup.AppGroup
import org.grayrat.powerscheduler.server.domain.appgroup.AppGroupId
import org.grayrat.powerscheduler.server.domain.appgroup.AppGroupQuery
import org.grayrat.powerscheduler.server.domain.appgroup.AppGroupRepository
import org.grayrat.powerscheduler.server.infrastructure.Bootstrap
import org.grayrat.powerscheduler.server.infrastructure.persistence.model.AppGroupEntity
import org.grayrat.powerscheduler.server.infrastructure.persistence.repository.impl.AppGroupJpaRepository
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

/**
 * @author grayrat
 * @since 2025/4/17
 */
@Transactional
@SpringBootTest(classes = [Bootstrap::class])
class AppGroupRepositoryImplIT(
    val appGroupJpaRepository: AppGroupJpaRepository,
    val appGroupRepository: AppGroupRepository,
) : FunSpec({

    context("test ${AppGroupRepositoryImpl::pageQuery.name}") {
        val appGroupSupplier = {
            generateSequence(1) { it + 1 }
                .take(15)
                .map { index ->
                    AppGroupEntity().apply {
                        this.code = "code-$index"
                        this.name = "name-$index"
                    }
                }
                .toList()
        }

        test("should succeed and return correct page") {
            val appGroupEntities = appGroupSupplier()
            appGroupJpaRepository.saveAll(appGroupEntities)
            val query = AppGroupQuery().apply {
                this.pageNo = 1
                this.pageSize = 10
            }
            val queryResult = shouldNotThrowAny {
                appGroupRepository.pageQuery(query)
            }
            val code2queryResultEntity = queryResult.content.associateBy { it.code }

            queryResult.number shouldBe query.pageNo
            queryResult.size shouldBe query.pageSize
            queryResult.totalElements shouldBe appGroupEntities.size.toLong()
            queryResult.totalPages shouldBe (appGroupEntities.size + query.pageSize - 1) / query.pageSize
            appGroupEntities.asSequence()
                .sortedByDescending { it.id }
                .take(query.pageSize)
                .shouldForAll {
                    val entity = code2queryResultEntity[it.code]
                    entity.shouldNotBeNull()
                    it.code shouldBe entity.code
                    it.name shouldBe entity.name
                }
        }

        test("should succeed and filter by code") {
            val appGroupEntities = appGroupSupplier()
            appGroupJpaRepository.saveAll(appGroupEntities)
            val query = AppGroupQuery().apply {
                this.pageNo = 1
                this.pageSize = 10
                this.code = appGroupEntities.random().code
            }
            val queryResult = shouldNotThrowAny {
                appGroupRepository.pageQuery(query)
            }
            queryResult.content.shouldForAll {
                it.code shouldContain query.code!!
            }
        }

        test("should succeed and filter by name") {
            val appGroupEntities = appGroupSupplier()
            appGroupJpaRepository.saveAll(appGroupEntities)
            val query = AppGroupQuery().apply {
                this.pageNo = 1
                this.pageSize = 10
                this.name = appGroupEntities.random().name
            }
            val queryResult = shouldNotThrowAny {
                appGroupRepository.pageQuery(query)
            }
            queryResult.content.shouldForAll {
                it.name shouldContain query.name!!
            }
        }
    }

    context("test ${AppGroupRepositoryImpl::existsByCode.name}") {
        test("should return false when code not exists") {
            val result = shouldNotThrowAny {
                appGroupRepository.existsByCode("")
            }
            result shouldBe false
        }

        test("should return true when code exists") {
            val appGroupEntity = AppGroupEntity().also {
                it.code = "testCode"
                it.name = "testName"
            }
            appGroupJpaRepository.save(appGroupEntity)

            val result = shouldNotThrowAny {
                appGroupRepository.existsByCode(appGroupEntity.code!!)
            }
            result shouldBe true
        }
    }

    context("test ${AppGroupRepositoryImpl::findByCode.name}") {
        test("should return null when code not exists") {
            val result = shouldNotThrowAny {
                appGroupRepository.findByCode("")
            }
            result.shouldBeNull()
        }

        test("should return true when code exists") {
            val appGroupEntity = AppGroupEntity().also {
                it.code = "testCode"
                it.name = "testName"
            }
            appGroupJpaRepository.save(appGroupEntity)

            val result = shouldNotThrowAny {
                appGroupRepository.findByCode(appGroupEntity.code!!)
            }
            result.shouldNotBeNull()
            result.id!!.value shouldBe appGroupEntity.id
            result.code shouldBe appGroupEntity.code
            result.name shouldBe appGroupEntity.name
        }
    }

    context("test ${AppGroupRepositoryImpl::save.name}") {
        test("should succeed when invoke 'insert' operation") {
            val appGroup = AppGroup().also {
                it.code = "testCode"
                it.name = "testName"
            }
            val result = shouldNotThrowAny {
                appGroupRepository.save(appGroup)
            }
            result.code shouldBe appGroup.code
            result.name shouldBe appGroup.name
        }

        test("should succeed when invoke 'edit' operation") {
            val appGroupEntity = AppGroupEntity().also {
                it.code = "old-testCode"
                it.name = "old-testName"
            }
            appGroupJpaRepository.save(appGroupEntity)

            val appGroup = AppGroup().also {
                it.id = AppGroupId(appGroupEntity.id!!)
                it.code = "testCode"
                it.name = "testName"
            }
            val result = shouldNotThrowAny {
                appGroupRepository.save(appGroup)
            }
            result.id shouldBe appGroup.id
            result.code shouldBe appGroup.code
            result.name shouldBe appGroup.name
        }
    }
})