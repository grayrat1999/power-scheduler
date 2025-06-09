package tech.powerscheduler.server.domain.domainevent

/**
 * @author grayrat
 * @since 2025/6/8
 */
interface DomainEventRepository {

    fun save(domainEvent: DomainEvent): DomainEventId

}