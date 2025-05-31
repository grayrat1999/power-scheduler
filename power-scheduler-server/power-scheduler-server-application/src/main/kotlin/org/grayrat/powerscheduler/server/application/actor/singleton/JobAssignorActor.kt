package org.grayrat.powerscheduler.server.application.actor.singleton

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.SupervisorStrategy
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive
import org.grayrat.powerscheduler.server.application.actor.JobSchedulerActor
import org.grayrat.powerscheduler.server.application.utils.hostPort
import org.grayrat.powerscheduler.server.application.utils.subscribeService
import org.grayrat.powerscheduler.server.domain.common.PageQuery
import org.grayrat.powerscheduler.server.domain.jobinfo.JobId
import org.grayrat.powerscheduler.server.domain.jobinfo.JobInfoRepository
import org.springframework.context.ApplicationContext
import org.springframework.transaction.support.TransactionTemplate
import java.time.Duration

/**
 * @author grayrat
 * @since 2025/5/14
 */
class JobAssignorActor(
    context: ActorContext<Command>,
    val jobInfoRepository: JobInfoRepository,
    val transactionTemplate: TransactionTemplate,
) : AbstractBehavior<JobAssignorActor.Command>(context) {

    sealed interface Command {
        class UpdateScheduler(
            val schedulers: Set<ActorRef<JobSchedulerActor.Command>>
        ) : Command

        object Assign : Command

        object ReassignFailRetry : Command

        object ReassignAllJob : Command
    }

    private val schedulerRegistry = mutableListOf<ActorRef<JobSchedulerActor.Command>>()
    private val assignFailedJobIds = mutableSetOf<JobId>()

    companion object {
        fun create(
            applicationContext: ApplicationContext,
        ): Behavior<Command> {
            val jobInfoRepository = applicationContext.getBean(JobInfoRepository::class.java)
            val transactionTemplate = applicationContext.getBean(TransactionTemplate::class.java)
            return Behaviors.setup { context ->
                Behaviors.withTimers { timer ->
                    val actor = JobAssignorActor(
                        context = context,
                        jobInfoRepository = jobInfoRepository,
                        transactionTemplate = transactionTemplate,
                    )
                    actor.apply {
                        subscribeService(JobSchedulerActor.SERVICE_KEY) {
                            Command.UpdateScheduler(it)
                        }
                    }
                    timer.startTimerWithFixedDelay(
                        Command.Assign,
                        Command.Assign,
                        Duration.ofSeconds(3),
                        Duration.ofSeconds(3),
                    )
                    timer.startTimerWithFixedDelay(
                        Command.ReassignFailRetry,
                        Command.ReassignFailRetry,
                        Duration.ofSeconds(3),
                        Duration.ofSeconds(3),
                    )
                    return@withTimers actor
                }
            }.apply {
                Behaviors.supervise(this).onFailure(SupervisorStrategy.resume())
            }
        }
    }

    override fun createReceive(): Receive<Command> {
        return newReceiveBuilder()
            .onMessage(Command.UpdateScheduler::class.java, this::handleUpdateSchedulers)
            .onMessageEquals(Command.Assign, this::handleAssign)
            .onMessageEquals(Command.ReassignFailRetry, this::handleReassignFailRetry)
            .onMessageEquals(Command.ReassignAllJob, this::handleReassignAllJob)
            .build()
    }

    private fun handleUpdateSchedulers(command: Command.UpdateScheduler): Behavior<Command> {
        schedulerRegistry.clear()
        schedulerRegistry.addAll(command.schedulers)
        context.self.tell(Command.ReassignAllJob)
        return this
    }

    private fun handleAssign(): Behavior<Command> {
        if (schedulerRegistry.isEmpty()) {
            context.log.error("handleAssign failed, schedulerRegistry is empty")
            return this
        }
        var pageNo = 1
        do {
            val query = PageQuery(pageNo = pageNo++, pageSize = 200)
            val page = jobInfoRepository.listAssignableIds(query)
            val jobIds = page.content
            reassignJob(jobIds)
        } while (page.isNotEmpty())
        return this
    }

    private fun handleReassignFailRetry(): Behavior<Command> {
        if (schedulerRegistry.isEmpty()) {
            context.log.error("handleReassignFailRetry failed, schedulerRegistry is empty")
            return this
        }
        reassignJob(assignFailedJobIds.toList())
        return this
    }

    private fun handleReassignAllJob(): Behavior<Command> {
        if (schedulerRegistry.isEmpty()) {
            context.log.error("handleReassignAllJob failed, schedulerRegistry is empty")
            return this
        }
        var pageNo = 1
        do {
            val query = PageQuery(pageNo = pageNo++, pageSize = 1000)
            val page = jobInfoRepository.listAllIds(query)
            val jobIds = page.content
            reassignJob(jobIds)
        } while (page.isNotEmpty())
        return this
    }

    private fun reassignJob(jobIds: List<JobId>) {
        if (jobIds.isEmpty()) {
            return
        }
        jobIds.forEachIndexed { index, jobId ->
            try {
                transactionTemplate.executeWithoutResult {
                    val jobInfo = jobInfoRepository.lockById(jobId)
                    if (jobInfo == null) {
                        return@executeWithoutResult
                    }
                    val schedulerIdx = jobIds.size % schedulerRegistry.size
                    val assignedScheduler = schedulerRegistry[schedulerIdx]
                    // 如果是本地actor, 则无法从ActorRef中获取ip和端口, 需要使用ActorSystem获取
                    jobInfo.schedulerAddress = assignedScheduler.hostPort() ?: context.system.hostPort()
                    jobInfoRepository.save(jobInfo)
                    context.log.info("assign job [{}] to server [{}]", jobInfo.id!!.value, jobInfo.schedulerAddress)
                }
                assignFailedJobIds.remove(jobId)
            } catch (e: Exception) {
                context.log.error("Failed to reassign job [{}]: {}", jobId.value, e.message, e)
                assignFailedJobIds.add(jobId)
            }
        }
    }
}