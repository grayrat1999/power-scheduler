package tech.powerscheduler.server.application.assembler

import org.springframework.stereotype.Component
import tech.powerscheduler.common.dto.request.WorkerRegisterRequestDTO
import tech.powerscheduler.server.application.dto.response.WorkerQueryResponseDTO
import tech.powerscheduler.server.domain.workerregistry.WorkerRegistry
import java.time.LocalDateTime
import java.util.*

/**
 * @author grayrat
 * @since 2025/5/21
 */
@Component
class WorkerRegistryAssembler {

    fun toDomainModel4RegisterRequestDTO(param: WorkerRegisterRequestDTO, remoteHost: String): WorkerRegistry {
        return WorkerRegistry().apply {
            this.appCode = param.appCode
            this.host = param.host.takeUnless { it.isNullOrBlank() } ?: remoteHost
            this.port = param.port
            this.accessToken = UUID.randomUUID().toString()
            this.lastHeartbeatAt = LocalDateTime.now()
        }
    }

    fun toWorkerQueryResponseDTO(workerRegistry: WorkerRegistry): WorkerQueryResponseDTO {
        return WorkerQueryResponseDTO().apply {
            this.appCode = workerRegistry.appCode
            this.host = workerRegistry.host
            this.port = workerRegistry.port
            this.lastHeartbeatAt = workerRegistry.lastHeartbeatAt
        }
    }
}