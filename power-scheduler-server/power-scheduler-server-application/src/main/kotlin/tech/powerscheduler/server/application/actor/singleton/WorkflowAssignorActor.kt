package tech.powerscheduler.server.application.actor.singleton

import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Receive
import org.springframework.transaction.support.TransactionTemplate
import tech.powerscheduler.server.domain.common.PageQuery
import tech.powerscheduler.server.domain.scheduler.Scheduler
import tech.powerscheduler.server.domain.scheduler.SchedulerRepository
import tech.powerscheduler.server.domain.workflow.WorkflowId
import tech.powerscheduler.server.domain.workflow.WorkflowRepository

/**
 * @author grayrat
 * @since 2025/7/11
 */
class WorkflowAssignorActor(
    context: ActorContext<Command>,
    private val workflowRepository: WorkflowRepository,
    private val schedulerRepository: SchedulerRepository,
    private val transactionTemplate: TransactionTemplate,
) : AbstractBehavior<WorkflowAssignorActor.Command>(context) {

    sealed interface Command {
        object Assign : Command

        object ReassignAll : Command
    }

    override fun createReceive(): Receive<Command> {
        return newReceiveBuilder()
            .onMessageEquals(Command.Assign, this::handleAssign)
            .onMessageEquals(Command.ReassignAll, this::handleReassignAllJob)
            .build()
    }

    private fun handleAssign(): Behavior<Command> {
        val availableSchedulers = schedulerRepository.findAll().filter { it.expired.not() }
        if (availableSchedulers.isEmpty()) {
            context.log.error("handleAssign failed, no available schedulers")
            return this
        }
        var pageNo = 1
        do {
            val query = PageQuery(pageNo = pageNo++, pageSize = 200)
            val page = workflowRepository.listAssignableIds(query)
            val jobIds = page.content
            reassignJob(jobIds, availableSchedulers)
        } while (page.isNotEmpty())
        return this
    }

    private fun handleReassignAllJob(): Behavior<Command> {
        val availableSchedulers = schedulerRepository.findAll().filter { it.expired.not() }
        if (availableSchedulers.isEmpty()) {
            context.log.error("handleReassignAllJob failed, no available schedulers")
            return this
        }
        var pageNo = 1
        do {
            val query = PageQuery(pageNo = pageNo++, pageSize = 1000)
            val page = workflowRepository.listAllIds(query)
            val jobIds = page.content
            reassignJob(jobIds, availableSchedulers)
        } while (page.isNotEmpty())
        return this
    }

    private fun reassignJob(workflowIds: List<WorkflowId>, availableSchedulers: List<Scheduler>) {
        if (workflowIds.isEmpty()) {
            return
        }
        workflowIds.forEachIndexed { index, workflowId ->
            try {
                transactionTemplate.executeWithoutResult {
                    val workflow = workflowRepository.lockById(workflowId)
                    if (workflow == null) {
                        return@executeWithoutResult
                    }
                    val schedulerIdx = workflowIds.size % availableSchedulers.size
                    val assignedScheduler = availableSchedulers[schedulerIdx]
                    workflow.schedulerAddress = assignedScheduler.address
                    workflowRepository.save(workflow)
                    context.log.info("assign workflow [{}] to server [{}]", workflow.id!!.value, workflow.schedulerAddress)
                }
            } catch (e: Exception) {
                context.log.error("Failed to reassign workflow [{}]: {}", workflowId.value, e.message, e)
            }
        }
    }
}