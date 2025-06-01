package tech.powerscheduler.server.application.service

import org.springframework.stereotype.Service
import tech.powerscheduler.common.exception.BizException
import tech.powerscheduler.server.application.assembler.AppGroupAssembler
import tech.powerscheduler.server.application.context.UserContext
import tech.powerscheduler.server.application.dto.request.AppGroupAddRequestDTO
import tech.powerscheduler.server.application.dto.request.AppGroupEditRequestDTO
import tech.powerscheduler.server.application.dto.request.AppGroupQueryRequestDTO
import tech.powerscheduler.server.application.dto.response.AppGroupQueryResponseDTO
import tech.powerscheduler.server.application.dto.response.PageDTO
import tech.powerscheduler.server.application.utils.toDTO
import tech.powerscheduler.server.domain.appgroup.AppGroupRepository

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