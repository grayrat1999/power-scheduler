package tech.powerscheduler.server.application.assembler

import org.springframework.stereotype.Component
import tech.powerscheduler.server.application.context.UserContext
import tech.powerscheduler.server.application.dto.request.AppGroupAddRequestDTO
import tech.powerscheduler.server.application.dto.request.AppGroupEditRequestDTO
import tech.powerscheduler.server.application.dto.request.AppGroupQueryRequestDTO
import tech.powerscheduler.server.application.dto.response.AppGroupQueryResponseDTO
import tech.powerscheduler.server.domain.appgroup.AppGroup
import tech.powerscheduler.server.domain.appgroup.AppGroupQuery
import java.time.LocalDateTime

/**
 * 应用分组相关模型映射
 *
 * @author grayrat
 * @since 2025/4/18
 */
@Component
class AppGroupAssembler {

    fun toDomainQuery(param: AppGroupQueryRequestDTO): AppGroupQuery {
        return AppGroupQuery().apply {
            this.name = param.name
            this.code = param.code
        }
    }

    fun toAppGroupQueryResponseDTO(model: AppGroup): AppGroupQueryResponseDTO {
        return AppGroupQueryResponseDTO().apply {
            this.code = model.code
            this.name = model.name
            this.secret = model.secret
            this.createdBy = model.createdBy
            this.createdAt = model.createdAt
        }
    }

    fun toDomainModel4AddRequest(
        param: AppGroupAddRequestDTO,
        userContext: UserContext,
    ): AppGroup {
        return AppGroup().apply {
            this.code = param.code
            this.name = param.name
            this.createdBy = userContext.userNo
            this.createdAt = LocalDateTime.now()
            this.updatedBy = userContext.userNo
            this.updatedAt = LocalDateTime.now()
        }
    }

    fun toDomainModel4EditRequest(
        model: AppGroup,
        param: AppGroupEditRequestDTO,
        userContext: UserContext,
    ): AppGroup {
        return AppGroup().apply {
            this.id = model.id
            this.code = model.code
            this.name = param.name
            this.createdBy = model.createdBy
            this.createdAt = model.createdAt
            this.updatedBy = userContext.userNo
            this.updatedAt = LocalDateTime.now()
        }
    }
}