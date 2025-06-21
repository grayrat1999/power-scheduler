package tech.powerscheduler.server.application.service

import org.springframework.stereotype.Service
import tech.powerscheduler.common.enums.JobStatusEnum
import tech.powerscheduler.server.application.dto.request.DashboardStatisticsInfoQueryRequestDTO
import tech.powerscheduler.server.application.dto.response.DashboardBasicInfoQueryResponseDTO
import tech.powerscheduler.server.application.dto.response.DashboardStatisticsInfoQueryResponseDTO
import tech.powerscheduler.server.domain.job.JobInfoRepository
import tech.powerscheduler.server.domain.job.JobInstanceRepository
import tech.powerscheduler.server.domain.worker.WorkerRegistryRepository

/**
 * @author grayrat
 * @since 2025/5/30
 */
@Service
class DashboardService(
    private val jobInfoRepository: JobInfoRepository,
    private val jobInstanceRepository: JobInstanceRepository,
    private val workerRegistryRepository: WorkerRegistryRepository,
) {

    fun queryBasicInfo(appCode: String?): DashboardBasicInfoQueryResponseDTO {
        val onlineWorkerCount = if (appCode.isNullOrEmpty()) {
            workerRegistryRepository.count()
        } else {
            workerRegistryRepository.countByAppCode(appCode)
        }
        val enabled2JobInfoCount = jobInfoRepository.countGroupedByEnabledWithAppCode(
            appCode = appCode.takeUnless { it.isNullOrBlank() },
        )
        return DashboardBasicInfoQueryResponseDTO(
            onlineWorkerCount = onlineWorkerCount,
            enabledJobInfoCount = enabled2JobInfoCount[true] ?: 0,
            disabledJobInfoCount = enabled2JobInfoCount[false] ?: 0,
        )
    }

    fun queryStatisticsInfo(param: DashboardStatisticsInfoQueryRequestDTO): DashboardStatisticsInfoQueryResponseDTO {
        val jobStatus2JobInstanceCount = jobInstanceRepository.countGroupedByJobStatusWithAppCode(
            appCode = param.appCode.takeUnless { it.isNullOrBlank() },
            scheduleAtRange = param.scheduleAtRange!!
        )
        return DashboardStatisticsInfoQueryResponseDTO(
            succeedJobInstanceCount = jobStatus2JobInstanceCount[JobStatusEnum.SUCCESS] ?: 0,
            failedJobInstanceCount = jobStatus2JobInstanceCount[JobStatusEnum.FAILED] ?: 0,
            jobInstanceCount = jobStatus2JobInstanceCount.values.sum()
        )
    }
}