package tech.powerscheduler.worker

import org.slf4j.LoggerFactory
import tech.powerscheduler.common.dto.request.JobDispatchRequestDTO
import tech.powerscheduler.common.dto.request.JobTerminateRequestDTO
import tech.powerscheduler.common.enums.JobStatusEnum
import tech.powerscheduler.common.enums.JobTypeEnum
import tech.powerscheduler.worker.persistence.TaskProgressEntity
import tech.powerscheduler.worker.persistence.TaskProgressRepository
import tech.powerscheduler.worker.task.ScriptTaskContext
import tech.powerscheduler.worker.task.Task
import tech.powerscheduler.worker.task.TaskContext
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
class TaskExecutorService {

    private val scheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(1)

    private val taskRegistry = HashMap<Long, Task>()
    private val taskQueue = BoundedDelayQueue<Task>(1000)
    private val workerThreadPool = ThreadPoolExecutor(
        10,
        10,
        0,
        TimeUnit.MILLISECONDS,
        ArrayBlockingQueue(1000),
        BasicThreadFactory("PS-Worker-"),
        ThreadPoolExecutor.AbortPolicy()
    )
    private val log = LoggerFactory.getLogger(TaskExecutorService::class.java)

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
            val job = taskQueue.poll()
            if (job == null) {
                break
            }
            workerThreadPool.execute {
                job.execute()
            }
        } while (true)
    }

    fun schedule(command: JobDispatchRequestDTO) {
        val taskContext = if (command.jobType == JobTypeEnum.SCRIPT) {
            ScriptTaskContext().apply {
                this.scriptType = command.scriptType
                this.scriptCode = command.scriptCode
            }
        } else {
            TaskContext()
        }
        taskContext.also {
            it.jobId = command.jobId
            it.jobInstanceId = command.jobInstanceId!!
            it.taskId = command.taskId
            it.executeParams = command.executeParams
            it.dataTime = command.dataTime
        }
        val task = Task(
            context = taskContext,
            scheduleAt = command.scheduleAt!!,
            processorPath = command.processor!!,
            priority = command.priority
        )
        val taskProgressEntity = TaskProgressEntity().also {
            it.jobId = command.jobId
            it.jobInstanceId = command.jobInstanceId!!
            it.taskId = command.taskId
        }
        if (taskQueue.offer(task).not()) {
            task.terminate()
            taskProgressEntity.also {
                it.startAt = LocalDateTime.now()
                it.startAt = LocalDateTime.now()
                it.status = JobStatusEnum.FAILED
                it.message = "job queue is full"
            }
        } else {
            taskRegistry.put(command.jobInstanceId!!, task)
            taskProgressEntity.status = JobStatusEnum.PENDING
        }
        TaskProgressRepository.save(taskProgressEntity)
    }

    fun terminate(param: JobTerminateRequestDTO) {
        val job = taskRegistry.remove(param.jobInstanceId)
        job?.terminate()
    }
}
