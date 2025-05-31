package org.grayrat.powerscheduler.server.application.actor

import akka.actor.typed.Behavior
import akka.actor.typed.SupervisorStrategy
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive
import akka.actor.typed.receptionist.ServiceKey
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.grayrat.powerscheduler.common.enums.JobStatusEnum
import org.grayrat.powerscheduler.server.application.utils.hostPort
import org.grayrat.powerscheduler.server.application.utils.registerSelfAsService
import org.grayrat.powerscheduler.server.domain.common.PageQuery
import org.grayrat.powerscheduler.server.domain.jobinfo.JobId
import org.grayrat.powerscheduler.server.domain.jobinfo.JobInfoRepository
import org.grayrat.powerscheduler.server.domain.jobinstance.JobInstanceRepository
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.transaction.support.TransactionTemplate
import java.time.Duration
import java.time.LocalDateTime

class JobSchedulerActor(
    context: ActorContext<Command>,
    val jobInfoRepository: JobInfoRepository,
    val jobInstanceRepository: JobInstanceRepository,
    val transactionTemplate: TransactionTemplate,
) : AbstractBehavior<JobSchedulerActor.Command>(context) {

    private val log = LoggerFactory.getLogger(javaClass)

    sealed interface Command {
        object ScheduleJobs : Command
    }

    companion object {

        val SERVICE_KEY: ServiceKey<Command> = ServiceKey.create<Command>(
            Command::class.java,
            JobSchedulerActor::class.simpleName
        )

        fun create(
            applicationContext: ApplicationContext,
        ): Behavior<Command> {
            val jobInfoRepository = applicationContext.getBean(JobInfoRepository::class.java)
            val jobInstanceRepository = applicationContext.getBean(JobInstanceRepository::class.java)
            val transactionTemplate = applicationContext.getBean(TransactionTemplate::class.java)
            return Behaviors.setup { context ->
                return@setup Behaviors.withTimers { timer ->
                    timer.startTimerAtFixedRate(
                        Command.ScheduleJobs,
                        Duration.ofSeconds(1)
                    )
                    val jobSchedulerActor = JobSchedulerActor(
                        context = context,
                        jobInfoRepository = jobInfoRepository,
                        jobInstanceRepository = jobInstanceRepository,
                        transactionTemplate = transactionTemplate,
                    )
                    jobSchedulerActor.apply {
                        this.registerSelfAsService(SERVICE_KEY)
                    }
                    return@withTimers jobSchedulerActor
                }
            }.apply {
                Behaviors.supervise(this).onFailure(SupervisorStrategy.resume())
            }
        }
    }

    override fun createReceive(): Receive<Command> {
        return newReceiveBuilder()
            .onMessageEquals(Command.ScheduleJobs) { return@onMessageEquals handleScheduleDueJobs() }
            .build()
    }

    private fun handleScheduleDueJobs(): Behavior<Command> {
        var pageNo = 1
        val currentServerAddress = context.system.hostPort()
        do {
            val pageQuery = PageQuery(pageNo = pageNo++, pageSize = 200)
            val assignedJobIdPage = jobInfoRepository.listIdsByEnabledAndSchedulerAddress(
                enabled = true,
                schedulerAddress = currentServerAddress,
                pageQuery = pageQuery
            )
            val assignedJobInfos = assignedJobIdPage.content
            if (assignedJobInfos.isEmpty()) {
                continue
            }
            val schedulableList = jobInfoRepository.findSchedulableByIds(
                ids = assignedJobInfos,
                baseTime = LocalDateTime.now()
            )
            if (schedulableList.isEmpty()) {
                continue
            }
            val jobIds = schedulableList.mapNotNull { it.id }
            scheduleJobs(jobIds, currentServerAddress)
        } while (assignedJobIdPage.isNotEmpty())
        return this
    }

    private fun scheduleJobs(
        jobIds: List<JobId>,
        currentServerAddress: String
    ) {
        // 使用管道限制最大并发数量, 为了避免并发大量的请求导致系统资源不足
        val channel = Channel<Unit>(20)
        runBlocking {
            val asyncScheduleJobs = jobIds.map { jobId ->
                async {
                    channel.send(Unit)
                    try {
                        schedulerOne(
                            jobId = jobId,
                            currentServerAddress = currentServerAddress
                        )
                    } catch (e: Exception) {
                        log.error("schedule job [{}] failed: {}", jobId.value, e.message, e)
                    } finally {
                        channel.receive()
                    }
                }
            }
            asyncScheduleJobs.awaitAll()
        }
    }

    fun schedulerOne(
        jobId: JobId,
        currentServerAddress: String,
    ) {
        transactionTemplate.executeWithoutResult {
            val jobInfoToSchedule = jobInfoRepository.lockById(jobId)
            val jobId2UnfinishedJobInstanceCount = jobInstanceRepository.countByJobIdAndJobStatus(
                jobIds = listOf(jobId),
                jobStatuses = JobStatusEnum.Companion.UNCOMPLETED_STATUSES
            )
            if (jobInfoToSchedule == null) {
                return@executeWithoutResult
            }
            if (jobInfoToSchedule.enabled!!.not()) {
                return@executeWithoutResult
            }
            val maxConcurrentNum = jobInfoToSchedule.maxConcurrentNum!!
            val existUnfinishedJobInstanceCount =
                jobId2UnfinishedJobInstanceCount[jobInfoToSchedule.id] ?: 0L
            if (existUnfinishedJobInstanceCount >= maxConcurrentNum) {
                return@executeWithoutResult
            }
            if (jobInfoToSchedule.nextScheduleAt == null) {
                jobInfoToSchedule.updateNextScheduleTime()
            }
            val jobInstance = jobInfoToSchedule.createInstance()
            jobInstance.jobStatus = JobStatusEnum.WAITING_DISPATCH
            jobInstance.schedulerAddress = currentServerAddress
            jobInfoToSchedule.updateNextScheduleTime()
            jobInfoRepository.save(jobInfoToSchedule)
            jobInstanceRepository.save(jobInstance)
        }
        log.info("schedule job [{}] success", jobId.value)
    }

}