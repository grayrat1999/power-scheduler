package tech.powerscheduler.server.application.event

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import tech.powerscheduler.common.dto.request.JobTerminateRequestDTO
import tech.powerscheduler.server.application.utils.CoroutineExecutor
import tech.powerscheduler.server.domain.common.PageQuery
import tech.powerscheduler.server.domain.jobinstance.JobInstanceRepository
import tech.powerscheduler.server.domain.jobinstance.JobInstanceTerminatedEvent
import tech.powerscheduler.server.domain.task.TaskRepository
import tech.powerscheduler.server.domain.worker.WorkerRemoteService

/**
 * @author grayrat
 * @since 2025/6/9
 */
@Component
class JobInstanceTerminatedEventListener(
    private val taskRepository: TaskRepository,
    private val jobInstanceRepository: JobInstanceRepository,
    private val workerRemoteService: WorkerRemoteService,
) {

    private val log = LoggerFactory.getLogger(JobInstanceTerminatedEventListener::class.java)

    private val executor = CoroutineExecutor(
        concurrency = 20,
        exceptionHandlePolicy = { e -> log.warn("", e) }
    )

    @EventListener
    fun onJobInstanceTerminated(evet: JobInstanceTerminatedEvent) {
        val jobInstanceId = evet.jobInstanceId
        val terminateParam = JobTerminateRequestDTO().apply {
            this.jobInstanceId = jobInstanceId.value
        }
        val jobInstance = jobInstanceRepository.findById(jobInstanceId) ?: return
        // TODO: 暂时写个2000条, 后面做MapReduce的时候再调整一下
        val taskPage = taskRepository.findAllByJobInstanceIdAndBatch(
            jobInstanceId = jobInstanceId,
            batch = jobInstance.batch!!,
            pageQuery = PageQuery(
                pageNo = 1,
                pageSize = 2000
            )
        )
        val tasks = taskPage.content
        log.info("jobInstance [{}] is terminated, start to terminate task", jobInstanceId.value)
        val tasksToTerminate = tasks.filterNot { it.isCompleted }
        val workerAddress2Tasks = tasksToTerminate.asSequence()
            .filterNot { it.workerAddress.isNullOrBlank() }
            .groupBy { it.workerAddress!! }

        workerAddress2Tasks.forEach { (workerAddress, tasks) ->
            executor.submit {
                try {
                    workerRemoteService.terminate(
                        baseUrl = workerAddress,
                        param = terminateParam
                    )
                    tasks.forEach { it.terminate() }
                    taskRepository.saveAll(tasks)
                    log.info("terminated [{}] tasks for job [{}] ", tasks.size, jobInstanceId.value)
                } catch (e: Exception) {
                    throw RuntimeException(
                        "Failed to terminate tasks in [$workerAddress] for jobInstance [${jobInstanceId.value}]",
                        e
                    )
                }
            }
        }
    }
}