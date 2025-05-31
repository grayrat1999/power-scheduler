package org.grayrat.powerscheduler.server.infrastructure.persistence.repository

import org.grayrat.powerscheduler.server.domain.common.AppCode
import org.grayrat.powerscheduler.server.domain.workerregistry.WorkerRegistry
import org.grayrat.powerscheduler.server.domain.workerregistry.WorkerRegistryId
import org.grayrat.powerscheduler.server.domain.workerregistry.WorkerRegistryRepository
import org.grayrat.powerscheduler.server.domain.workerregistry.WorkerRegistryUniqueKey
import org.grayrat.powerscheduler.server.infrastructure.persistence.model.WorkerRegistryEntity
import org.grayrat.powerscheduler.server.infrastructure.persistence.repository.impl.WorkerRegistryJpaRepository
import org.grayrat.powerscheduler.server.infrastructure.utils.toDomainModel
import org.grayrat.powerscheduler.server.infrastructure.utils.toEntity
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
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

    override fun count(): Long {
        return workerRegistryJpaRepository.count()
    }

    override fun countByAppCode(appCode: String): Long {
        return workerRegistryJpaRepository.countByAppCode(appCode)
    }

    override fun lockById(id: WorkerRegistryId): WorkerRegistry? {
        val entity = workerRegistryJpaRepository.findByIdForUpdate(id.value)
        return entity?.toDomainModel()
    }

    override fun findAllByAppCode(appCode: String): List<WorkerRegistry> {
        val entities = workerRegistryJpaRepository.findAllByAppCodeIn(listOf(appCode))
        return entities.map { it.toDomainModel() }
    }

    override fun findAllByAppCodes(appCodes: Iterable<String>): Map<AppCode, List<WorkerRegistry>> {
        val entities = workerRegistryJpaRepository.findAllByAppCodeIn(appCodes)
        return entities.map { it.toDomainModel() }.groupBy { it.appCode!! }
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
            val appCodeEqual = criteriaBuilder.equal(
                root.get<WorkerRegistryEntity>(WorkerRegistryEntity::appCode.name),
                uniqueKey.appCode,
            )
            val hostEqual = criteriaBuilder.equal(
                root.get<WorkerRegistryEntity>(WorkerRegistryEntity::host.name),
                uniqueKey.host,
            )
            val portEqual = criteriaBuilder.equal(
                root.get<WorkerRegistryEntity>(WorkerRegistryEntity::port.name),
                uniqueKey.port,
            )
            val predicates = listOfNotNull(appCodeEqual, hostEqual, portEqual)
            criteriaBuilder.and(*predicates.toTypedArray())
        }
        val workerRegistryEntity = workerRegistryJpaRepository.findOne(specification).getOrNull()
        return workerRegistryEntity?.toDomainModel()
    }

    override fun save(workerRegistry: WorkerRegistry): WorkerRegistryId {
        val entityToSave = workerRegistry.toEntity()
        workerRegistryJpaRepository.save(entityToSave)
        return WorkerRegistryId(entityToSave.id!!)
    }

    override fun delete(id: WorkerRegistryId) {
        workerRegistryJpaRepository.deleteById(id.value)
    }

    override fun deleteAll(ids: Iterable<WorkerRegistryId>) {
        workerRegistryJpaRepository.deleteAllByIdInBatch(ids.map { it.value })
    }

}