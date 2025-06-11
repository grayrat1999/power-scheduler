package tech.powerscheduler.server.application.actor

import akka.actor.typed.Behavior
import akka.actor.typed.PostStop
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
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.transaction.support.TransactionTemplate
import tech.powerscheduler.common.enums.ExecuteModeEnum.*
import tech.powerscheduler.common.enums.JobStatusEnum
import tech.powerscheduler.common.enums.ScheduleTypeEnum
import tech.powerscheduler.server.application.utils.hostPort
import tech.powerscheduler.server.application.utils.registerSelfAsService
import tech.powerscheduler.server.domain.common.PageQuery
import tech.powerscheduler.server.domain.jobinfo.JobId
import tech.powerscheduler.server.domain.jobinfo.JobInfoRepository
import tech.powerscheduler.server.domain.jobinstance.JobInstance
import tech.powerscheduler.server.domain.jobinstance.JobInstanceRepository
import tech.powerscheduler.server.domain.task.Task
import tech.powerscheduler.server.domain.task.TaskRepository
import tech.powerscheduler.server.domain.workerregistry.WorkerRegistry
import tech.powerscheduler.server.domain.workerregistry.WorkerRegistryRepository
import java.time.Duration
import java.time.LocalDateTime

class JobSchedulerActor(
    context: ActorContext<Command>,
    private val taskRepository: TaskRepository,
    private val jobInfoRepository: JobInfoRepository,
    private val jobInstanceRepository: JobInstanceRepository,
    private val workerRegistryRepository: WorkerRegistryRepository,
    private val transactionTemplate: TransactionTemplate,
) : AbstractBehavior<JobSchedulerActor.Command>(context) {

    private val log = LoggerFactory.getLogger(javaClass)

    sealed interface Command {
        object ScheduleJobs : Command

        object CreateTasks : Command
    }

    companion object {

        val SERVICE_KEY: ServiceKey<Command> = ServiceKey.create<Command>(
            Command::class.java,
            JobSchedulerActor::class.simpleName
        )

        fun create(
            applicationContext: ApplicationContext,
        ): Behavior<Command> {
            val taskRepository = applicationContext.getBean(TaskRepository::class.java)
            val jobInfoRepository = applicationContext.getBean(JobInfoRepository::class.java)
            val jobInstanceRepository = applicationContext.getBean(JobInstanceRepository::class.java)
            val workerRegistryRepository = applicationContext.getBean(WorkerRegistryRepository::class.java)
            val transactionTemplate = applicationContext.getBean(TransactionTemplate::class.java)
            return Behaviors.setup { context ->
                return@setup Behaviors.withTimers { timer ->
                    timer.startTimerAtFixedRate(
                        Command.ScheduleJobs,
                        Duration.ofSeconds(1)
                    )
                    timer.startTimerAtFixedRate(
                        Command.CreateTasks,
                        Duration.ofSeconds(1)
                    )
                    val jobSchedulerActor = JobSchedulerActor(
                        context = context,
                        taskRepository = taskRepository,
                        jobInfoRepository = jobInfoRepository,
                        jobInstanceRepository = jobInstanceRepository,
                        workerRegistryRepository = workerRegistryRepository,
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
            .onMessageEquals(Command.CreateTasks) { return@onMessageEquals handleCreateTasks() }
            .onSignal(PostStop::class.java) { signal -> onPostStop() }
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
            if (jobInfoToSchedule == null) {
                return@executeWithoutResult
            }
            if (jobInfoToSchedule.enabled!!.not()) {
                return@executeWithoutResult
            }
            // 对于第一次调度的任务, 初始化下次调度时间
            if (jobInfoToSchedule.nextScheduleAt == null) {
                jobInfoToSchedule.updateNextScheduleTime()
                jobInfoRepository.save(jobInfoToSchedule)
                return@executeWithoutResult
            }
            // 检查任务实例并发数量
            val jobId2UnfinishedJobInstanceCount = jobInstanceRepository.countByJobIdAndJobStatus(
                jobIds = listOf(jobId),
                jobStatuses = JobStatusEnum.Companion.UNCOMPLETED_STATUSES
            )
            val maxConcurrentNum = jobInfoToSchedule.maxConcurrentNum!!
            val existUnfinishedJobInstanceCount = jobId2UnfinishedJobInstanceCount[jobInfoToSchedule.id] ?: 0L
            if (existUnfinishedJobInstanceCount >= maxConcurrentNum) {
                return@executeWithoutResult
            }
            // 检查当前可用机器, 如果没有可用机器，则跳过本次调度(TODO: 系统告警)
            val appCode = jobInfoToSchedule.appCode
            val availableWorkers = workerRegistryRepository.findAllByAppCode(appCode!!)
            if (availableWorkers.isEmpty()) {
                jobInfoToSchedule.apply {
                    // 固定延迟的调度模式由于调度取消无法更新上次完成时间, 所以用本次调度时间为基准设置下次调度时间
                    if (scheduleType == ScheduleTypeEnum.FIX_DELAY) {
                        this.nextScheduleAt = this.nextScheduleAt!!.plusSeconds(this.scheduleConfig!!.toLong())
                    } else {
                        this.updateNextScheduleTime()
                    }
                }
                jobInfoRepository.save(jobInfoToSchedule)
                log.info(
                    "schedule job [{}] cancel for no available workers, nextScheduleTime={}",
                    jobId.value, jobInfoToSchedule.nextScheduleAt
                )
                return@executeWithoutResult
            }
            // 前置检查全部通过后, 正式开始调度
            val jobInstance = jobInfoToSchedule.createInstance()
            jobInstance.jobStatus = JobStatusEnum.WAITING_SCHEDULE
            jobInstance.schedulerAddress = currentServerAddress
            jobInfoToSchedule.apply {
                /*
                    对于固定延迟的调度模式, 先以本次调度时间为基准设置下次调度时间(避免本次调度的任务完成前, 调度方法总是将任务查出来)
                    在任务完成后再真正更新下次调度时间，之所以可以这么做是: 任务完成时间 + 固定延迟 > 任务调度时间 + 固定延迟
                 */
                if (scheduleType == ScheduleTypeEnum.FIX_DELAY) {
                    this.nextScheduleAt = this.nextScheduleAt!!.plusSeconds(this.scheduleConfig!!.toLong())
                } else {
                    updateNextScheduleTime()
                }
            }
            jobInfoRepository.save(jobInfoToSchedule)
            jobInstanceRepository.save(jobInstance)
            log.info("schedule job [{}] success, nextScheduleTime={}", jobId.value, jobInfoToSchedule.nextScheduleAt)
        }
    }

    private fun handleCreateTasks(): Behavior<Command> {
        var pageNo = 1
        val currentServerAddress = context.system.hostPort()
        do {
            val pageQuery = PageQuery(pageNo = pageNo++, pageSize = 200)
            val jobIdPage = jobInfoRepository.listIdsByEnabledAndSchedulerAddress(
                enabled = null,
                schedulerAddress = currentServerAddress,
                pageQuery = pageQuery
            )
            if (jobIdPage.isEmpty()) {
                break
            }
            val jobIds = jobIdPage.content
            createTasks(jobIds)
        } while (jobIdPage.isNotEmpty())
        return this
    }

    private fun createTasks(jobIds: List<JobId>) {
        var pageNo = 1
        do {
            val pageQuery = PageQuery(pageNo = pageNo++, pageSize = 200)
            val jobInstanceIdPage = jobInstanceRepository.listDispatchable(
                jobIds = jobIds,
                pageQuery = pageQuery
            )
            if (jobInstanceIdPage.isEmpty()) {
                break
            }
            val jobInstanceIds = jobInstanceIdPage.content
            val appCode2AvailableWorkers = mutableMapOf<String, List<WorkerRegistry>>()
            jobInstanceIds.forEach { jobInstanceId ->
                transactionTemplate.executeWithoutResult {
                    val jobInstance = jobInstanceRepository.lockById(jobInstanceId)
                    if (jobInstance == null) {
                        return@executeWithoutResult
                    }
                    if (jobInstance.jobStatus != JobStatusEnum.WAITING_SCHEDULE) {
                        return@executeWithoutResult
                    }
                    val appCode = jobInstance.appCode!!
                    val workerRegistries = appCode2AvailableWorkers.computeIfAbsent(appCode) { appCode ->
                        workerRegistryRepository.findAllByAppCode(appCode)
                    }
                    jobInstance.jobStatus = JobStatusEnum.WAITING_DISPATCH
                    val tasks = doCreateTasks(jobInstance, workerRegistries)
                    jobInstanceRepository.save(jobInstance)
                    taskRepository.saveAll(tasks)
                }
            }
        } while (jobInstanceIdPage.isNotEmpty())
    }

    private fun doCreateTasks(jobInstance: JobInstance, availableWorkers: List<WorkerRegistry>): List<Task> {
        val tasks = when (jobInstance.executeMode!!) {
            // 单机模式创建1个task
            // Map/MapReduce模式 先创建1个task，后续根据任务上报的结果持续创建子task(需要做好幂等)
            SINGLE, MAP_REDUCE -> {
                val targetWorker = availableWorkers.random()
                val task = jobInstance.createTask(targetWorker.address)
                listOf(task)
            }
            // 广播模式 有多少台在线的worker就创建多少个task
            BROADCAST -> {
                availableWorkers.map { jobInstance.createTask(it.address) }
            }
        }
        return tasks
    }

    private fun onPostStop(): Behavior<Command> {
        log.info("start to reset jobs assigned to this server")
        val currentServerAddress = context.system.hostPort()
        jobInfoRepository.clearSchedulerAddress(currentServerAddress)
        log.info("successfully reset jobs assigned to this server")
        return this
    }
}