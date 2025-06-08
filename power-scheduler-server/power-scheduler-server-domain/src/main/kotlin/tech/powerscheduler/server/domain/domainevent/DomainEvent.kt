package tech.powerscheduler.server.domain.domainevent

/**
 * @author grayrat
 * @since 2025/6/8
 */
class DomainEvent {
    /**
     * 主键id
     */
    var id: DomainEventId? = null

    /**
     * 聚合根id, 格式: DomainModelName-ModelId
     */
    var aggregateId: String? = null

    /**
     * 事件类型
     */
    var eventType: DomainEventTypeEnum? = null

    /**
     * 事件状态
     */
    var eventStatus: DomainEventStatusEnum? = null

    /**
     * 事件内容
     */
    var payload: String? = null

    /**
     * 重试次数
     */
    var retryCnt: Int? = null
}