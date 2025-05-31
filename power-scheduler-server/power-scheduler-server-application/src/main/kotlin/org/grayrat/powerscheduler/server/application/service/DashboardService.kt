package org.grayrat.powerscheduler.server.application.service

import org.grayrat.powerscheduler.common.enums.JobStatusEnum
import org.grayrat.powerscheduler.server.application.dto.request.DashboardStatisticsInfoQueryRequestDTO
import org.grayrat.powerscheduler.server.application.dto.response.DashboardBasicInfoQueryResponseDTO
import org.grayrat.powerscheduler.server.application.dto.response.DashboardStatisticsInfoQueryResponseDTO
import org.grayrat.powerscheduler.server.domain.jobinfo.JobInfoRepository
import org.grayrat.powerscheduler.server.domain.jobinstance.JobInstanceRepository
import org.grayrat.powerscheduler.server.domain.workerregistry.WorkerRegistryRepository
import org.springframework.stereotype.Service

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