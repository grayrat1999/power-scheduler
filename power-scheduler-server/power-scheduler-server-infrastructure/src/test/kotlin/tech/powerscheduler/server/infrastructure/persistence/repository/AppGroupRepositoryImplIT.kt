package tech.powerscheduler.server.infrastructure.persistence.repository

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import tech.powerscheduler.server.domain.appgroup.AppGroup
import tech.powerscheduler.server.domain.appgroup.AppGroupId
import tech.powerscheduler.server.domain.appgroup.AppGroupQuery
import tech.powerscheduler.server.domain.appgroup.AppGroupRepository
import tech.powerscheduler.server.infrastructure.Bootstrap
import tech.powerscheduler.server.infrastructure.persistence.model.AppGroupEntity
import tech.powerscheduler.server.infrastructure.persistence.model.NamespaceEntity
import tech.powerscheduler.server.infrastructure.persistence.repository.impl.AppGroupJpaRepository
import tech.powerscheduler.server.infrastructure.persistence.repository.impl.NamespaceJpaRepository
import tech.powerscheduler.server.infrastructure.utils.toDomainModel

/**
 * @author grayrat
 * @since 2025/4/17
 */
@Transactional
@SpringBootTest(classes = [Bootstrap::class])
class AppGroupRepositoryImplIT(
    val namespaceJpaRepository: NamespaceJpaRepository,
    val appGroupJpaRepository: AppGroupJpaRepository,
    val appGroupRepository: AppGroupRepository,
) : FunSpec({

    context("test ${AppGroupRepositoryImpl::pageQuery.name}") {
        val namespaceSupplier = {
            NamespaceEntity().apply {
                this.code = "namespaceCode"
                this.name = "namespaceName"
                this.description = "namespaceDescription"
            }
        }

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
            val namespace = namespaceSupplier()
            namespaceJpaRepository.save(namespace)
            val appGroupEntities = appGroupSupplier().onEach {
                it.namespaceEntity = namespace
            }
            appGroupJpaRepository.saveAll(appGroupEntities)
            val query = AppGroupQuery().apply {
                this.pageNo = 1
                this.pageSize = 10
                this.namespaceCode = namespace.code
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
            val namespace = namespaceSupplier()
            namespaceJpaRepository.save(namespace)
            val appGroupEntities = appGroupSupplier().onEach {
                it.namespaceEntity = namespace
            }
            appGroupJpaRepository.saveAll(appGroupEntities)
            val query = AppGroupQuery().apply {
                this.pageNo = 1
                this.pageSize = 10
                this.namespaceCode = namespace.code
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
            val namespace = namespaceSupplier()
            namespaceJpaRepository.save(namespace)
            val appGroupEntities = appGroupSupplier().onEach {
                it.namespaceEntity = namespace
            }
            appGroupJpaRepository.saveAll(appGroupEntities)
            val query = AppGroupQuery().apply {
                this.pageNo = 1
                this.pageSize = 10
                this.namespaceCode = namespace.code
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

    context("test ${AppGroupRepositoryImpl::findByCode.name}") {
        test("should return null when code not exists") {
            val namespaceEntity = NamespaceEntity().apply {
                code = "namespaceCode"
                name = "namespaceName"
            }
            namespaceJpaRepository.save(namespaceEntity)
            val result = shouldNotThrowAny {
                appGroupRepository.findByCode(namespaceEntity.toDomainModel(), "")
            }
            result.shouldBeNull()
        }

        test("should return true when code exists") {
            val namespaceEntity = NamespaceEntity().apply {
                this.code = "namespaceCode"
                this.name = "namespaceName"
            }
            namespaceJpaRepository.save(namespaceEntity)
            val appGroupEntity = AppGroupEntity().also {
                it.namespaceEntity = namespaceEntity
                it.code = "testCode"
                it.name = "testName"
            }
            appGroupJpaRepository.save(appGroupEntity)

            val result = shouldNotThrowAny {
                appGroupRepository.findByCode(namespaceEntity.toDomainModel(), appGroupEntity.code!!)
            }
            result.shouldNotBeNull()
            result.id!!.value shouldBe appGroupEntity.id
            result.code shouldBe appGroupEntity.code
            result.name shouldBe appGroupEntity.name
        }
    }

    context("test ${AppGroupRepositoryImpl::save.name}") {
        test("should succeed when invoke 'insert' operation") {
            val namespaceEntity = NamespaceEntity().apply {
                this.code = "namespaceCode"
                this.name = "namespaceName"
            }
            namespaceJpaRepository.save(namespaceEntity)
            val appGroup = AppGroup().also {
                it.namespace = namespaceEntity.toDomainModel()
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
            val namespaceEntity = NamespaceEntity().apply {
                this.code = "namespaceCode"
                this.name = "namespaceName"
            }
            namespaceJpaRepository.save(namespaceEntity)
            val appGroupEntity = AppGroupEntity().also {
                it.namespaceEntity = namespaceEntity
                it.code = "old-testCode"
                it.name = "old-testName"
            }
            appGroupJpaRepository.save(appGroupEntity)

            val appGroup = AppGroup().also {
                it.namespace = namespaceEntity.toDomainModel()
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