package org.grayrat.powerscheduler.server.application.service

import org.grayrat.powerscheduler.common.exception.BizException
import org.grayrat.powerscheduler.server.application.assembler.AppGroupAssembler
import org.grayrat.powerscheduler.server.application.context.UserContext
import org.grayrat.powerscheduler.server.application.dto.request.AppGroupAddRequestDTO
import org.grayrat.powerscheduler.server.application.dto.request.AppGroupEditRequestDTO
import org.grayrat.powerscheduler.server.application.dto.request.AppGroupQueryRequestDTO
import org.grayrat.powerscheduler.server.application.dto.response.AppGroupQueryResponseDTO
import org.grayrat.powerscheduler.server.application.dto.response.PageDTO
import org.grayrat.powerscheduler.server.application.utils.toDTO
import org.grayrat.powerscheduler.server.domain.appgroup.AppGroupRepository
import org.springframework.stereotype.Service

/**
 * @author grayrat
 * @since 2025/4/16
 */
@Service
class AppGroupService(
    private val appGroupRepository: AppGroupRepository,
    private val appGroupAssembler: AppGroupAssembler,
) {

    fun list(param: AppGroupQueryRequestDTO): PageDTO<AppGroupQueryResponseDTO> {
        val query = appGroupAssembler.toDomainQuery(param)
        val page = appGroupRepository.pageQuery(query)
        return page.toDTO().map { appGroupAssembler.toAppGroupQueryResponseDTO(it) }
    }

    fun add(param: AppGroupAddRequestDTO, userContext: UserContext): Long {
        val exist = appGroupRepository.existsByCode(param.code!!)
        if (exist) {
            throw BizException(message = "应用保存失败, 应用编码[${param.code}]已存在")
        }
        val appGroupToSave = appGroupAssembler.toDomainModel4AddRequest(param, userContext)
        val appGroupSaved = appGroupRepository.save(appGroupToSave)
        return appGroupSaved.id?.value!!
    }

    fun edit(param: AppGroupEditRequestDTO, userContext: UserContext) {
        val appGroup = appGroupRepository.findByCode(param.code!!)
            ?: throw BizException(message = "应用保存失败, 应用分组不存在")
        val appGroupToEdit = appGroupAssembler.toDomainModel4EditRequest(
            model = appGroup,
            param = param,
            userContext = userContext
        )
        appGroupRepository.save(appGroupToEdit)
    }

}