package tech.powerscheduler.server.infrastructure.persistence.repository

import org.springframework.stereotype.Repository
import tech.powerscheduler.server.domain.domainevent.DomainEvent
import tech.powerscheduler.server.domain.domainevent.DomainEventId
import tech.powerscheduler.server.domain.domainevent.DomainEventRepository
import tech.powerscheduler.server.infrastructure.persistence.repository.impl.DomainEventJpaRepository
import tech.powerscheduler.server.infrastructure.utils.toEntity

/**
 * @author grayrat
 * @since 2025/6/8
 */
@Repository
class DomainEventRepositoryImpl(
    private val domainEventJpaRepository: DomainEventJpaRepository
) : DomainEventRepository {

    override fun save(domainEvent: DomainEvent): DomainEventId {
        val entity = domainEvent.toEntity()
        domainEventJpaRepository.save(entity)
        return DomainEventId(entity.id!!)
    }

}