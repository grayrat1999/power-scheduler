package tech.powerscheduler.worker

import org.slf4j.LoggerFactory
import tech.powerscheduler.common.dto.request.JobDispatchRequestDTO
import tech.powerscheduler.common.dto.request.JobTerminateRequestDTO
import tech.powerscheduler.common.enums.JobStatusEnum
import tech.powerscheduler.common.enums.JobTypeEnum
import tech.powerscheduler.worker.job.Job
import tech.powerscheduler.worker.job.JobContext
import tech.powerscheduler.worker.job.ScriptJobContext
import tech.powerscheduler.worker.persistence.JobProgressEntity
import tech.powerscheduler.worker.persistence.JobProgressRepository
import tech.powerscheduler.worker.util.BasicThreadFactory
import tech.powerscheduler.worker.util.BoundedDelayQueue
import java.time.LocalDateTime
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * 任务执行服务
 *
 * @author grayrat
 * @since 2025/5/22
 */
class JobExecutorService {

    private val scheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(1)

    private val jobRegistry = HashMap<Long, Job>()
    private val jobQueue = BoundedDelayQueue<Job>(1000)
    private val workerThreadPool = ThreadPoolExecutor(
        10,
        10,
        0,
        TimeUnit.MILLISECONDS,
        ArrayBlockingQueue(1000),
        BasicThreadFactory("PS-Worker-"),
        ThreadPoolExecutor.AbortPolicy()
    )
    private val log = LoggerFactory.getLogger(JobExecutorService::class.java)

    fun start() {
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(
            this::onTick,
            1,
            1,
            TimeUnit.SECONDS
        )
    }

    fun stop() {
        scheduledThreadPoolExecutor.shutdown()
        log.info("[PowerScheduler] {} stopped", javaClass.simpleName)
    }

    private fun onTick() {
        do {
            val job = jobQueue.poll()
            if (job == null) {
                break
            }
            workerThreadPool.execute {
                job.execute()
            }
        } while (true)
    }

    fun schedule(command: JobDispatchRequestDTO) {
        val jobContext = if (command.jobType == JobTypeEnum.SCRIPT) {
            ScriptJobContext().apply {
                this.scriptType = command.scriptType
                this.scriptCode = command.scriptCode
            }
        } else {
            JobContext()
        }
        jobContext.also {
            it.jobId = command.jobId
            it.jobInstanceId = command.jobInstanceId!!
            it.taskId = command.taskId
            it.executeParams = command.executeParams
            it.dataTime = command.dataTime
        }
        val job = Job(
            context = jobContext,
            scheduleAt = command.scheduleAt!!,
            processorPath = command.processor!!,
            priority = command.priority
        )
        val jobProgressEntity = JobProgressEntity().also {
            it.jobId = command.jobId
            it.jobInstanceId = command.jobInstanceId!!
            it.taskId = command.taskId
        }
        if (jobQueue.offer(job).not()) {
            job.terminate()
            jobProgressEntity.also {
                it.startAt = LocalDateTime.now()
                it.startAt = LocalDateTime.now()
                it.status = JobStatusEnum.FAILED
                it.message = "job queue is full"
            }
        } else {
            jobRegistry.put(command.jobInstanceId!!, job)
            jobProgressEntity.status = JobStatusEnum.PENDING
        }
        JobProgressRepository.save(jobProgressEntity)
    }

    fun terminate(param: JobTerminateRequestDTO) {
        val job = jobRegistry.remove(param.jobInstanceId)
        job?.terminate()
    }
}
