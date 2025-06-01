package tech.powerscheduler.worker

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.slf4j.LoggerFactory
import tech.powerscheduler.common.dto.request.JobProgressReportRequestDTO
import tech.powerscheduler.worker.persistence.JobProgressRepository
import tech.powerscheduler.worker.util.ExecutorCoroutineScope

/**
 * 任务进度上报服务
 *
 * @author grayrat
 * @since 2025/5/25
 */
class JobProgressReportService(
    /**
     * server发现服务
     */
    private val serverDiscoveryService: ServerDiscoveryService,
) {

    /**
     * 启用标志
     */
    @Volatile
    private var isActive = true

    /**
     * Http客户端
     */
    private val httpClient = PowerSchedulerWorkerHttpClient()

    private val log = LoggerFactory.getLogger(JobProgressReportService::class.java)

    /**
     * 启用任务进度上报服务
     */
    fun start() = ExecutorCoroutineScope.launch {
        while (isActive) {
            try {
                this.reportProgress()
            } catch (_: CancellationException) {
                log.info("[Powerscheduler] reportProgress canceled")
            } catch (e: Exception) {
                log.warn("[Powerscheduler] reportProgress failed: {}", e.message, e)
            }
        }
    }

    /**
     * 关闭任务进度上报服务
     */
    fun stop() {
        isActive = false
        ExecutorCoroutineScope.cancelJobs()
        log.info("[PowerScheduler] {} stopped", javaClass.simpleName)
    }

    suspend fun CoroutineScope.reportProgress() {
        val jobInstanceIdSet = JobProgressRepository.listDistinctJobInstanceIds()
        if (jobInstanceIdSet.isEmpty()) {
            delay(300)
            return
        }
        if (serverDiscoveryService.availableServerUrls.isEmpty()) {
            delay(300)
            return
        }
        val channel = Channel<Unit>(10)
        val deferredList = jobInstanceIdSet.map { jobInstanceId ->
            async {
                try {
                    channel.send(Unit)
                    doReportProgress(jobInstanceId)
                } catch (e: Exception) {
                    log.warn("[Powerscheduler] reportProgress failed: {}", e.message, e)
                } finally {
                    channel.receive()
                }
            }
        }
        deferredList.awaitAll()
    }

    fun doReportProgress(jobInstanceId: Long) {
        val serverUrl = serverDiscoveryService.availableServerUrls.randomOrNull()
        if (serverUrl == null) {
            log.warn("[Powerscheduler] reportProgress failed: no available server]")
            return
        }
        val jobProgressList = JobProgressRepository.listByJobInstanceId(jobInstanceId)
        val latestJobProgress = jobProgressList.sortedByDescending { it.id }.first()
        val param = JobProgressReportRequestDTO().apply {
            this.jobInstanceId = latestJobProgress.jobInstanceId
            this.startAt = latestJobProgress.startAt
            this.endAt = latestJobProgress.endAt
            this.jobStatus = latestJobProgress.status
            this.message = latestJobProgress.message
        }
        val result = httpClient.reportProgress(baseUrl = serverUrl, param = param)
        if (result.success && result.data == true) {
            log.debug(
                "[Powerscheduler] reportProgress successful: jobInstanceId={}, jobStatus={}",
                jobInstanceId, latestJobProgress.status
            )
            val ids = jobProgressList.mapNotNull { it.id }
            JobProgressRepository.deleteByIds(ids)
        } else {
            log.warn("[Powerscheduler] reportProgress failed: {}", result.message, result.cause)
        }
    }
}