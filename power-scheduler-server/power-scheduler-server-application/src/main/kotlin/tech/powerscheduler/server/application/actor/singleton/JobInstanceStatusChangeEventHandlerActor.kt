package tech.powerscheduler.server.application.actor.singleton

import akka.actor.typed.Behavior
import akka.actor.typed.SupervisorStrategy
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.transaction.support.TransactionTemplate
import tech.powerscheduler.server.application.service.JobInstanceService
import tech.powerscheduler.server.application.utils.JSON
import tech.powerscheduler.server.domain.common.PageQuery
import tech.powerscheduler.server.domain.domainevent.DomainEventRepository
import tech.powerscheduler.server.domain.domainevent.DomainEventStatusEnum
import tech.powerscheduler.server.domain.domainevent.DomainEventTypeEnum
import tech.powerscheduler.server.domain.job.JobInstanceId
import tech.powerscheduler.server.domain.job.JobInstanceRepository
import tech.powerscheduler.server.domain.workflow.WorkflowNodeInstanceStatusChangeEvent
import java.time.Duration

/**
 * @author grayrat
 * @since 2025/6/25
 */
class JobInstanceStatusChangeEventHandlerActor(
    context: ActorContext<Command>,
    val domainEventRepository: DomainEventRepository,
    val jobInstanceRepository: JobInstanceRepository,
    val transactionTemplate: TransactionTemplate,
) : AbstractBehavior<JobInstanceStatusChangeEventHandlerActor.Command>(context) {

    private val log = LoggerFactory.getLogger(javaClass)

    sealed interface Command {
        object HandleEvent : Command
    }

    companion object {
        fun create(
            applicationContext: ApplicationContext,
        ): Behavior<Command> {
            val domainEventRepository = applicationContext.getBean(DomainEventRepository::class.java)
            val jobInstanceRepository = applicationContext.getBean(JobInstanceRepository::class.java)
            val jobInstanceService = applicationContext.getBean(JobInstanceService::class.java)
            val transactionTemplate = applicationContext.getBean(TransactionTemplate::class.java)
            return Behaviors.setup { context ->
                Behaviors.withTimers { timer ->
                    val actor = JobInstanceStatusChangeEventHandlerActor(
                        context = context,
                        domainEventRepository = domainEventRepository,
                        jobInstanceRepository = jobInstanceRepository,
                        transactionTemplate = transactionTemplate,
                    )
                    timer.startTimerWithFixedDelay(
                        Command.HandleEvent,
                        Command.HandleEvent,
                        Duration.ofSeconds(3),
                        Duration.ofSeconds(1),
                    )
                    return@withTimers actor
                }
            }.apply {
                Behaviors.supervise(this).onFailure(SupervisorStrategy.resume())
            }
        }
    }

    override fun createReceive(): Receive<Command?>? {
        return newReceiveBuilder()
            .onMessageEquals(Command.HandleEvent) { handleEvent() }
            .build()
    }

    fun handleEvent(): Behavior<Command> {
        var pageNo = 1
        do {
            val pageQuery = PageQuery(pageNo = pageNo++, pageSize = 200)
            val eventPage = domainEventRepository.findPendingList(
                eventType = DomainEventTypeEnum.WORKFLOW_NODE_INSTANCE_STATUS_CHANGED,
                pageQuery = pageQuery,
            )
            if (eventPage.isEmpty()) {
                return this
            }
            val events = eventPage.content
            val aggregateId2eventGroup = events.groupBy { it.aggregateId }
            aggregateId2eventGroup.entries.forEach { (_, events) ->
                val firstEvent = events.first()
                val domainEventIdsToDelete = events.mapNotNull { it.id }.filterNot { it.value == firstEvent.id!!.value }
                domainEventRepository.deleteByIds(domainEventIdsToDelete)
                try {
                    firstEvent.eventStatus = DomainEventStatusEnum.PROCESSING
                    domainEventRepository.save(firstEvent)
                    val event = JSON.readValue<WorkflowNodeInstanceStatusChangeEvent>(firstEvent.body)!!
                    doHandleEvent(event)
                    firstEvent.eventStatus = DomainEventStatusEnum.SUCCESS
                    domainEventRepository.save(firstEvent)
                } catch (e: Exception) {
                    if (firstEvent.canRetry) {
                        firstEvent.resetStatusForRetry()
                    } else {
                        firstEvent.eventStatus = DomainEventStatusEnum.FAILED
                    }
                    domainEventRepository.save(firstEvent)
                    log.error("handle TaskStatusChangeEvent [{}] failed: {}", firstEvent.id!!.value, e.message, e)
                }
            }
        } while (eventPage.isNotEmpty())
        return this
    }

    private fun doHandleEvent(event: WorkflowNodeInstanceStatusChangeEvent) {
        val jobInstanceId = JobInstanceId(event.workflowInstanceId)
        val jobInstance = jobInstanceRepository.findById(jobInstanceId)!!
        // 更新工作流节点实例的状态

        // 如果工作流节点实例到达了完成状态, 则进一步判断并执行后续操作

        // 如果当前节点的后续节点 依赖的所有节点都成功了, 则继续推进后续的节点

        // 如果有节点实例失败了, 或者所有节点实例都完成了, 则进一步更新工作流实例的状态
    }
}