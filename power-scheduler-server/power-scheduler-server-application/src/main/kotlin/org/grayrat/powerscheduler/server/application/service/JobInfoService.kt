package org.grayrat.powerscheduler.server.application.service

import org.grayrat.powerscheduler.common.exception.BizException
import org.grayrat.powerscheduler.server.application.assembler.JobInfoAssembler
import org.grayrat.powerscheduler.server.application.dto.request.JobInfoAddRequestDTO
import org.grayrat.powerscheduler.server.application.dto.request.JobInfoEditRequestDTO
import org.grayrat.powerscheduler.server.application.dto.request.JobInfoQueryRequestDTO
import org.grayrat.powerscheduler.server.application.dto.request.JobSwitchRequestDTO
import org.grayrat.powerscheduler.server.application.dto.response.JobInfoDetailResponseDTO
import org.grayrat.powerscheduler.server.application.dto.response.JobInfoQueryResponseDTO
import org.grayrat.powerscheduler.server.application.dto.response.PageDTO
import org.grayrat.powerscheduler.server.application.utils.toDTO
import org.grayrat.powerscheduler.server.domain.appgroup.AppGroupRepository
import org.grayrat.powerscheduler.server.domain.jobinfo.JobId
import org.grayrat.powerscheduler.server.domain.jobinfo.JobInfoQuery
import org.grayrat.powerscheduler.server.domain.jobinfo.JobInfoRepository
import org.springframework.stereotype.Service

/**
 * 任务相关服务
 *
 * @author grayrat
 * @since 2025/4/16
 */
@Service
class JobInfoService(
    private val appGroupRepository: AppGroupRepository,
    private val jobInfoRepository: JobInfoRepository,
    private val jobInfoAssembler: JobInfoAssembler,
) {

    fun query(param: JobInfoQueryRequestDTO): PageDTO<JobInfoQueryResponseDTO> {
        val query = JobInfoQuery().apply {
            this.appCode = param.appCode
            this.jobName = param.jobName
            this.processor = param.processor
        }
        val page = jobInfoRepository.pageQuery(query)
        return page.toDTO().map { jobInfoAssembler.toJobInfoQueryResponseDTO(it) }
    }

    fun detail(jobId: Long): JobInfoDetailResponseDTO? {
        val jobInfo = jobInfoRepository.findById(JobId(jobId))
        return jobInfo?.let { jobInfoAssembler.toJobInfoDetailResponseDTO(it) }
    }

    fun add(param: JobInfoAddRequestDTO): Long {
        val appGroup = appGroupRepository.findByCode(param.appCode!!)
            ?: throw BizException(message = "任务新增失败: 应用分组不存在")
        val jobInfo = jobInfoAssembler.toDomainModel4AddRequest(param, appGroup).apply {
            validScheduleConfig()
        }
        val jobId = jobInfoRepository.save(jobInfo)
        return jobId.value
    }

    fun edit(param: JobInfoEditRequestDTO) {
        val jobId = JobId(param.jobId!!)
        val jobInfo = jobInfoRepository.findById(jobId)
            ?: throw BizException(message = "任务保存失败: 任务不存在")
        val jobInfoToSave = jobInfoAssembler.toDomainModel4EditRequest(jobInfo, param)
        try {
            jobInfoToSave.validScheduleConfig()
        } catch (e: Exception) {
            throw BizException(e.message)
        }
        jobInfoRepository.save(jobInfoToSave)
    }

    fun switch(param: JobSwitchRequestDTO) {
        val jobId = JobId(param.jobId!!)
        val jobInfo = jobInfoRepository.findById(jobId)
            ?: throw BizException("任务保存失败: 任务不存在")
        if (jobInfo.enabled == param.enabled) {
            return
        }
        jobInfo.enabled = param.enabled
        if (jobInfo.enabled == true) {
            jobInfo.updateNextScheduleTime()
        } else {
            jobInfo.nextScheduleAt = null
            jobInfo.schedulerAddress = null
        }
        jobInfoRepository.save(jobInfo)
    }

    fun remove(jobId: Long) {
        val jobIdToRemove = JobId(jobId)
        val jobInfo = jobInfoRepository.findById(jobIdToRemove)
        if (jobInfo == null) {
            return
        }
        jobInfoRepository.deleteById(jobIdToRemove)
    }

}