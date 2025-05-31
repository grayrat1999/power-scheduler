package org.grayrat.powerscheduler.server.application.actor.singleton

import akka.actor.typed.Behavior
import akka.actor.typed.SupervisorStrategy
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive
import org.grayrat.powerscheduler.server.application.service.WorkerLifeCycleService
import org.grayrat.powerscheduler.server.domain.workerregistry.WorkerRegistryRepository
import org.springframework.context.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime

/**
 * @author grayrat
 * @since 2025/5/24
 */
class WorkerRegistryCleanActor(
    context: ActorContext<Command>,
    private val workerLifeCycleService: WorkerLifeCycleService,
    private val workerRegistryRepository: WorkerRegistryRepository,
) : AbstractBehavior<WorkerRegistryCleanActor.Command>(context) {

    sealed interface Command {
        object CleanDueWorkerRegistry : Command
    }

    companion object {
        fun create(applicationContext: ApplicationContext): Behavior<Command> {
            val workerLifeCycleService = applicationContext.getBean(WorkerLifeCycleService::class.java)
            val workerRegistryRepository = applicationContext.getBean(WorkerRegistryRepository::class.java)
            return Behaviors.setup { context ->
                Behaviors.withTimers { timer ->
                    val actor = WorkerRegistryCleanActor(
                        context = context,
                        workerLifeCycleService = workerLifeCycleService,
                        workerRegistryRepository = workerRegistryRepository,
                    )
                    timer.startTimerWithFixedDelay(
                        Command.CleanDueWorkerRegistry,
                        Command.CleanDueWorkerRegistry,
                        Duration.ofSeconds(0),
                        Duration.ofSeconds(1),
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
            .onMessageEquals(Command.CleanDueWorkerRegistry, this::handleCleanDueWorkerRegistry)
            .build()
    }

    fun handleCleanDueWorkerRegistry(): Behavior<Command> {
        // 若5s内没有收到心跳，则认为节点已失效，应当被清理掉
        val expiredAt = LocalDateTime.now().minusSeconds(5)
        val expiredWorkerRegistries = workerRegistryRepository.findAllExpired(expiredAt)
        expiredWorkerRegistries.forEach {
            try {
                workerLifeCycleService.removeWorkerRegistry(it)
                context.log.info("Cleaned due worker registry [{}]", it.address)
            } catch (e: Exception) {
                context.log.warn("Failed to remove worker registry [{}]: {}", it.address, e.message, e)
            }
        }
        return this
    }

}