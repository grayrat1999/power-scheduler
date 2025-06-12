package tech.powerscheduler.server.application.event

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import tech.powerscheduler.common.enums.ExecuteModeEnum.*
import tech.powerscheduler.server.application.service.JobInstanceService
import tech.powerscheduler.server.domain.domainevent.DomainEvent
import tech.powerscheduler.server.domain.domainevent.DomainEventRepository
import tech.powerscheduler.server.domain.domainevent.DomainEventStatusEnum
import tech.powerscheduler.server.domain.domainevent.DomainEventTypeEnum
import tech.powerscheduler.server.domain.task.TaskStatusChangeEvent

/**
 * @author grayrat
 * @since 2025/6/8
 */
@Component
class TaskStatusChangeEventListener(
    private val domainEventRepository: DomainEventRepository,
    private val jobInstanceService: JobInstanceService
) {

    @EventListener
    fun onTaskStatusChange(event: TaskStatusChangeEvent) {
        when (event.executeMode) {
            SINGLE, BROADCAST -> updateJobInstanceProgressNow(event)
            MAP_REDUCE -> persistentEvent(event)
        }
    }

    fun persistentEvent(event: TaskStatusChangeEvent) {
        val domainEvent = DomainEvent().apply {
            this.eventStatus = DomainEventStatusEnum.PENDING
            this.aggregateId = "Task-${event.taskId.value}"
            this.eventType = DomainEventTypeEnum.TASK_STATUS_CHANGED
            this.payload = ""
            this.retryCnt = 0
        }
        domainEventRepository.save(domainEvent)
    }

    fun updateJobInstanceProgressNow(event: TaskStatusChangeEvent) {
        jobInstanceService.updateJobInstanceProgress(event.jobInstanceId)
    }
}