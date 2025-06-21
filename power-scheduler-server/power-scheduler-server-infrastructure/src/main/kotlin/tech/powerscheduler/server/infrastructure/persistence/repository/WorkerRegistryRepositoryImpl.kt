package tech.powerscheduler.server.infrastructure.persistence.repository

import org.springframework.data.jpa.domain.Specification
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Repository
import tech.powerscheduler.server.application.exception.OptimisticLockingConflictException
import tech.powerscheduler.server.domain.appgroup.AppGroupKey
import tech.powerscheduler.server.domain.worker.WorkerRegistry
import tech.powerscheduler.server.domain.worker.WorkerRegistryId
import tech.powerscheduler.server.domain.worker.WorkerRegistryRepository
import tech.powerscheduler.server.domain.worker.WorkerRegistryUniqueKey
import tech.powerscheduler.server.infrastructure.persistence.model.WorkerRegistryEntity
import tech.powerscheduler.server.infrastructure.persistence.repository.impl.WorkerRegistryJpaRepository
import tech.powerscheduler.server.infrastructure.utils.toDomainModel
import tech.powerscheduler.server.infrastructure.utils.toEntity
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull

/**
 * @author grayrat
 * @since 2025/4/29
 */
@Repository
class WorkerRegistryRepositoryImpl(
    private val workerRegistryJpaRepository: WorkerRegistryJpaRepository
) : WorkerRegistryRepository {

    override fun countByNamespaceCodeAndAppCode(namespaceCode: String, appCode: String): Long {
        return if (appCode.isEmpty()) {
            workerRegistryJpaRepository.countByNamespaceCode(namespaceCode)
        } else {
            workerRegistryJpaRepository.countByNamespaceCodeAndAppCode(
                namespaceCode = namespaceCode,
                appCode = appCode
            )
        }
    }

    override fun lockById(id: WorkerRegistryId): WorkerRegistry? {
        val entity = workerRegistryJpaRepository.findByIdForUpdate(id.value)
        return entity?.toDomainModel()
    }

    override fun findAllByAppGroupKey(groupKey: AppGroupKey): List<WorkerRegistry> {
        return findAllByAppGroupKeys(listOf(groupKey)).values.firstOrNull().orEmpty()
    }

    override fun findAllByAppGroupKeys(
        groupKeys: Collection<AppGroupKey>
    ): Map<AppGroupKey, List<WorkerRegistry>> {
        if (groupKeys.isEmpty()) {
            return emptyMap()
        }
        val specification = Specification<WorkerRegistryEntity> { root, _, criteriaBuilder ->
            val predicates = groupKeys.map {
                criteriaBuilder.and(
                    criteriaBuilder.equal(
                        root.get<String>(WorkerRegistryEntity::namespaceCode.name), it.namespaceCode
                    ),
                    criteriaBuilder.equal(
                        root.get<String>(WorkerRegistryEntity::appCode.name), it.appCode
                    ),
                )
            }
            criteriaBuilder.or(*predicates.toTypedArray())
        }
        val entities = workerRegistryJpaRepository.findAll(specification)
        return entities.map { it.toDomainModel() }.groupBy {
            AppGroupKey(
                namespaceCode = it.namespaceCode!!,
                appCode = it.appCode!!
            )
        }
    }

    override fun findAllExpired(expiredAt: LocalDateTime): List<WorkerRegistry> {
        val specification = Specification<WorkerRegistryEntity> { root, _, criteriaBuilder ->
            val lastHeartbeatAtLessThan = criteriaBuilder.lessThan(
                root.get(WorkerRegistryEntity::lastHeartbeatAt.name),
                expiredAt
            )
            val predicates = listOfNotNull(lastHeartbeatAtLessThan)
            criteriaBuilder.and(*predicates.toTypedArray())
        }
        val workerRegistryEntity = workerRegistryJpaRepository.findAll(specification)
        return workerRegistryEntity.map { it.toDomainModel() }
    }

    override fun findByUk(uniqueKey: WorkerRegistryUniqueKey): WorkerRegistry? {
        val specification = Specification<WorkerRegistryEntity> { root, _, criteriaBuilder ->
            val hostEqual = criteriaBuilder.equal(
                root.get<WorkerRegistryEntity>(WorkerRegistryEntity::host.name),
                uniqueKey.host,
            )
            val portEqual = criteriaBuilder.equal(
                root.get<WorkerRegistryEntity>(WorkerRegistryEntity::port.name),
                uniqueKey.port,
            )
            val predicates = listOfNotNull(hostEqual, portEqual)
            criteriaBuilder.and(*predicates.toTypedArray())
        }
        val workerRegistryEntity = workerRegistryJpaRepository.findOne(specification).getOrNull()
        return workerRegistryEntity?.toDomainModel()
    }

    override fun findByAccessToken(accessToken: String): WorkerRegistry? {
        val entity = workerRegistryJpaRepository.findByAccessToken(accessToken)
        return entity?.toDomainModel()
    }

    override fun save(workerRegistry: WorkerRegistry): WorkerRegistryId {
        val entityToSave = workerRegistry.toEntity()
        try {
            workerRegistryJpaRepository.save(entityToSave)
            return WorkerRegistryId(entityToSave.id!!)
        } catch (e: ObjectOptimisticLockingFailureException) {
            throw OptimisticLockingConflictException()
        }
    }

    override fun delete(id: WorkerRegistryId) {
        workerRegistryJpaRepository.deleteById(id.value)
    }

    override fun deleteAll(ids: Iterable<WorkerRegistryId>) {
        workerRegistryJpaRepository.deleteAllByIdInBatch(ids.map { it.value })
    }

}