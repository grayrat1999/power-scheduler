package tech.powerscheduler.server.application.assembler

import org.springframework.stereotype.Component
import tech.powerscheduler.server.application.dto.request.NamespaceAddRequestDTO
import tech.powerscheduler.server.application.dto.request.NamespaceEditRequestDTO
import tech.powerscheduler.server.application.dto.request.NamespaceQueryRequestDTO
import tech.powerscheduler.server.application.dto.response.NamespaceQueryResponseDTO
import tech.powerscheduler.server.domain.namespace.Namespace
import tech.powerscheduler.server.domain.namespace.NamespaceQuery

/**
 * @author grayrat
 * @since 2025/6/21
 */
@Component
class NamespaceAssembler {

    fun toDomainQuery(param: NamespaceQueryRequestDTO): NamespaceQuery {
        return NamespaceQuery().apply {
            this.name = param.name
            this.pageNo = param.pageNo
            this.pageSize = param.pageSize
        }
    }

    fun toNamespaceQueryResponseDTO(domainModel: Namespace): NamespaceQueryResponseDTO {
        return NamespaceQueryResponseDTO().apply {
            this.id = domainModel.id!!.value
            this.code = domainModel.code
            this.name = domainModel.name
            this.description = domainModel.description
            this.createdBy = domainModel.createdBy
            this.createdAt = domainModel.createdAt
        }
    }

    fun toDomainModel4AddRequestDTO(param: NamespaceAddRequestDTO): Namespace {
        return Namespace().apply {
            this.code = param.code
            this.name = param.name
            this.description = param.description
        }
    }

    fun toDomainModel4EditRequestDTO(domainModel: Namespace, param: NamespaceEditRequestDTO): Namespace {
        return Namespace().apply {
            this.id = domainModel.id
            this.code = domainModel.code
            this.name = param.name
            this.description = param.description
        }
    }

}