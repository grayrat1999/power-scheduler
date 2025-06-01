package tech.powerscheduler.server.application.actor

import akka.actor.typed.Behavior
import akka.actor.typed.SupervisorStrategy
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import tech.powerscheduler.common.enums.JobStatusEnum
import tech.powerscheduler.server.application.assembler.JobInstanceAssembler
import tech.powerscheduler.server.application.utils.hostPort
import tech.powerscheduler.server.domain.common.PageQuery
import tech.powerscheduler.server.domain.jobinfo.JobId
import tech.powerscheduler.server.domain.jobinfo.JobInfoRepository
import tech.powerscheduler.server.domain.jobinstance.JobInstance
import tech.powerscheduler.server.domain.jobinstance.JobInstanceRepository
import tech.powerscheduler.server.domain.worker.WorkerRemoteService
import tech.powerscheduler.server.domain.workerregistry.WorkerRegistryRepository
import java.time.Duration
import java.time.LocalDateTime

/**
 * @author grayrat
 * @since 2025/5/14
 */
class JobDispatcherActor(
    context: ActorContext<Command>,
    private val jobInstanceAssembler: JobInstanceAssembler,
    private val jobInfoRepository: JobInfoRepository,
    private val jobInstanceRepository: JobInstanceRepository,
    private val workerRegistryRepository: WorkerRegistryRepository,
    private val workerRemoteService: WorkerRemoteService,
) : AbstractBehavior<JobDispatcherActor.Command>(context) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    sealed interface Command {
        object DispatchJobs : Command
    }

    companion object {
        fun create(
            applicationContext: ApplicationContext,
        ): Behavior<Command> {
            val jobInstanceRepository = applicationContext.getBean(JobInstanceRepository::class.java)
            val jobInstanceAssembler = applicationContext.getBean(JobInstanceAssembler::class.java)
            val workerRegistryRepository = applicationContext.getBean(WorkerRegistryRepository::class.java)
            val workerRemoteService = applicationContext.getBean(WorkerRemoteService::class.java)
            val jobInfoRepository = applicationContext.getBean(JobInfoRepository::class.java)
            return Behaviors.setup { context ->
                return@setup Behaviors.withTimers { timer ->
                    timer.startTimerAtFixedRate(
                        Command.DispatchJobs,
                        Duration.ofSeconds(1)
                    )
                    val jobDispatcherActor = JobDispatcherActor(
                        context = context,
                        jobInstanceAssembler = jobInstanceAssembler,
                        jobInfoRepository = jobInfoRepository,
                        jobInstanceRepository = jobInstanceRepository,
                        workerRegistryRepository = workerRegistryRepository,
                        workerRemoteService = workerRemoteService,
                    )
                    return@withTimers jobDispatcherActor
                }
            }.apply {
                Behaviors.supervise(this).onFailure(SupervisorStrategy.resume())
            }
        }
    }

    override fun createReceive(): Receive<Command> {
        return newReceiveBuilder()
            .onMessageEquals(Command.DispatchJobs) { this.handleDispatchJob() }
            .build()
    }

    private fun handleDispatchJob(): Behavior<Command> {
        var pageNo = 1
        val currentServerAddress = context.system.hostPort()
        do {
            val pageQuery = PageQuery(pageNo = pageNo++, pageSize = 200)
            val assignedJobIdPage = jobInfoRepository.listIdsByEnabledAndSchedulerAddress(
                enabled = null,
                schedulerAddress = currentServerAddress,
                pageQuery = pageQuery,
            )
            val assignedJobInfos = assignedJobIdPage.content
            if (assignedJobInfos.isEmpty()) {
                continue
            }
            dispatchByIds(assignedJobInfos)
            pageNo++
        } while (assignedJobIdPage.isNotEmpty())
        return this
    }

    private fun dispatchByIds(assignedJobInfos: List<JobId>) {
        var pageNo = 1
        do {
            val dispatchablePage = jobInstanceRepository.listDispatchable(
                jobIds = assignedJobInfos,
                pageQuery = PageQuery(pageNo = pageNo++, pageSize = 200)
            )
            val dispatchableJobInstanceList = dispatchablePage.content
            dispatch(dispatchableJobInstanceList)
        } while (dispatchablePage.isNotEmpty())
    }

    private fun dispatch(dispatchableList: List<JobInstance>) {
        if (dispatchableList.isEmpty()) {
            return
        }
        val appCode2DispatchableList = dispatchableList.groupBy { it.appCode!! }.toMutableMap()
        val appCodes = appCode2DispatchableList.keys
        val appCode2WorkerRegistry = workerRegistryRepository.findAllByAppCodes(appCodes)

        // 使用管道限制最大并发数量, 为了避免并发大量的请求导致系统资源不足
        val channel = Channel<Unit>(1)
        runBlocking {
            val asyncDispatchJobs = dispatchableList.map { jobInstance ->
                async {
                    channel.send(Unit)
                    val appCode = jobInstance.appCode
                    val candidateWorkers = appCode2WorkerRegistry[appCode].orEmpty().map { it.address }.toSet()
                    try {
                        dispatchOne(jobInstance, candidateWorkers)
                    } catch (e: Exception) {
                        log.error("dispatch jobInstance [{}] failed: {}", jobInstance.id!!.value, e.message, e)
                    } finally {
                        channel.receive()
                    }
                }
            }
            asyncDispatchJobs.awaitAll()
        }
    }

    fun dispatchOne(jobInstance: JobInstance, candidateWorkers: Set<String>) {
        if (candidateWorkers.isEmpty()) {
            if (jobInstance.canReattempt) {
                jobInstance.resetStatusForReattempt()
            } else {
                jobInstance.startAt = LocalDateTime.now()
                jobInstance.endAt = LocalDateTime.now()
                jobInstance.jobStatus = JobStatusEnum.FAILED
                jobInstance.message = "no available worker"
            }
            jobInstanceRepository.save(jobInstance)
            return
        }
        val targetWorker = if (jobInstance.attemptCnt!! > 0 && candidateWorkers.size > 1) {
            // 如果上次任务派发失败了，本次就换个节点派发
            (candidateWorkers - jobInstance.workerAddress.orEmpty()).random()
        } else {
            // 如果是首次派发，或者就只有一个节点，那就只能派发到该节点
            candidateWorkers.random()
        }
        jobInstance.workerAddress = targetWorker
        jobInstance.schedulerAddress = context.system.hostPort()
        jobInstanceRepository.save(jobInstance)
        val dispatchJobRequestDTO = jobInstanceAssembler.toDispatchJobRequestDTO(jobInstance)
        val result = workerRemoteService.dispatch(targetWorker, dispatchJobRequestDTO)
        if (result.success && result.data == true) {
            jobInstance.jobStatus = JobStatusEnum.DISPATCHING
            log.info("dispatch jobInstance [{}] to worker [{}] successfully", jobInstance.id!!.value, targetWorker)
        } else {
            val exception = result.cause
            log.error(
                "dispatch jobInstance [{}] to {} failed: {}",
                jobInstance.id!!.value, targetWorker, exception?.message, exception
            )
            jobInstance.apply {
                if (this.canReattempt) {
                    this.resetStatusForReattempt()
                } else {
                    this.startAt = LocalDateTime.now()
                    this.endAt = LocalDateTime.now()
                    this.jobStatus = JobStatusEnum.FAILED
                    this.message = exception?.stackTraceToString()?.take(2000)
                }
            }
        }
        jobInstanceRepository.save(jobInstance)
    }

}