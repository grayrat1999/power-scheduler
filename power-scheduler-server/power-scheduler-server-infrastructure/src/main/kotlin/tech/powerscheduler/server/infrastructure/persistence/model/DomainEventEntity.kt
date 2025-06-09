package tech.powerscheduler.server.infrastructure.persistence.model

import jakarta.persistence.*
import tech.powerscheduler.server.domain.domainevent.DomainEventStatusEnum
import tech.powerscheduler.server.domain.domainevent.DomainEventTypeEnum

/**
 * 领域事件实体
 *
 * @author grayrat
 * @since 2025/6/8
 */
@Entity
@Table(name = "domain_event")
class DomainEventEntity : BaseEntity() {

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    var id: Long? = null

    /**
     * 聚合根id, 格式: DomainModelName-ModelId
     */
    @Column(name = "aggregate_id", nullable = false, updatable = false)
    var aggregateId: String? = null

    /**
     * 事件类型
     */
    @Column(name = "event_type", nullable = false, updatable = false)
    var eventType: DomainEventTypeEnum? = null

    /**
     * 事件状态
     */
    @Column(name = "event_status", nullable = false, updatable = false)
    var eventStatus: DomainEventStatusEnum? = null

    /**
     * 事件内容
     */
    @Column(name = "payload", updatable = false)
    var payload: String? = null

    /**
     * 重试次数
     */
    @Column(name = "retry_cnt")
    var retryCnt: Int? = null
}