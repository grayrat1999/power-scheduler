package tech.powerscheduler.server.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tech.powerscheduler.common.enums.JobTypeEnum
import tech.powerscheduler.common.exception.BizException
import tech.powerscheduler.server.application.assembler.JobInfoAssembler
import tech.powerscheduler.server.application.dto.request.JobInfoAddRequestDTO
import tech.powerscheduler.server.application.dto.request.JobInfoEditRequestDTO
import tech.powerscheduler.server.application.dto.request.JobInfoQueryRequestDTO
import tech.powerscheduler.server.application.dto.request.JobSwitchRequestDTO
import tech.powerscheduler.server.application.dto.response.JobInfoDetailResponseDTO
import tech.powerscheduler.server.application.dto.response.JobInfoQueryResponseDTO
import tech.powerscheduler.server.application.dto.response.PageDTO
import tech.powerscheduler.server.application.utils.toDTO
import tech.powerscheduler.server.domain.appgroup.AppGroupRepository
import tech.powerscheduler.server.domain.jobinfo.JobId
import tech.powerscheduler.server.domain.jobinfo.JobInfoQuery
import tech.powerscheduler.server.domain.jobinfo.JobInfoRepository

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
        if (param.jobType == JobTypeEnum.SCRIPT) {
            throw BizException(message = "为了保证系统安全, 在线试用不允许用户创建脚本的类型任务")
        }
        val appGroup = appGroupRepository.findByCode(param.appCode!!)
            ?: throw BizException(message = "任务新增失败: 应用分组不存在")
        val jobInfo = jobInfoAssembler.toDomainModel4AddRequest(param, appGroup).apply {
            validScheduleConfig()
        }
        val jobId = jobInfoRepository.save(jobInfo)
        return jobId.value
    }

    @Transactional
    fun edit(param: JobInfoEditRequestDTO) {
        val jobId = JobId(param.jobId!!)
        val jobInfo = jobInfoRepository.lockById(jobId)
            ?: throw BizException(message = "任务保存失败: 任务不存在")
        if (param.jobType == JobTypeEnum.SCRIPT) {
            if (jobInfo.scriptCode != param.scriptCode) {
                throw BizException(message = "为了保证系统安全, 在线试用不允许用户修改脚本内容")
            }
        }
        val jobInfoToSave = jobInfoAssembler.toDomainModel4EditRequest(jobInfo, param).apply {
            validScheduleConfig()
        }
        jobInfoRepository.save(jobInfoToSave)
    }

    @Transactional
    fun switch(param: JobSwitchRequestDTO) {
        val jobId = JobId(param.jobId!!)
        val jobInfo = jobInfoRepository.lockById(jobId)
            ?: throw BizException("任务保存失败: 任务不存在")
        if (jobInfo.enabled == param.enabled) {
            return
        }
        jobInfo.enabled = param.enabled
        if (jobInfo.enabled == true) {
            jobInfo.initNextScheduleTime()
        } else {
            jobInfo.nextScheduleAt = null
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