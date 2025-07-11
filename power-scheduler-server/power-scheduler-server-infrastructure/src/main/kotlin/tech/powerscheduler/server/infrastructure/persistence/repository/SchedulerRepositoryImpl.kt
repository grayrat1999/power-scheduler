package tech.powerscheduler.server.infrastructure.persistence.repository

import tech.powerscheduler.server.domain.scheduler.Scheduler
import tech.powerscheduler.server.domain.scheduler.SchedulerId
import tech.powerscheduler.server.domain.scheduler.SchedulerRepository
import tech.powerscheduler.server.infrastructure.persistence.repository.impl.SchedulerJpaRepository
import tech.powerscheduler.server.infrastructure.utils.toDomain
import tech.powerscheduler.server.infrastructure.utils.toEntity

/**
 * @author grayrat
 * @since 2025/7/11
 */
class SchedulerRepositoryImpl(
    private val schedulerJpaRepository: SchedulerJpaRepository,
) : SchedulerRepository {

    override fun findByAddress(address: String): Scheduler? {
        val entity = schedulerJpaRepository.findByAddress(address)
        return entity?.toDomain()
    }

    override fun save(scheduler: Scheduler): SchedulerId {
        val entity = scheduler.toEntity()
        schedulerJpaRepository.save(entity)
        return SchedulerId(entity.id!!)
    }

}