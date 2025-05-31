package org.grayrat.powerscheduler.server.application.service

import org.grayrat.powerscheduler.common.dto.request.WorkerHeartbeatRequestDTO
import org.grayrat.powerscheduler.common.dto.request.WorkerRegisterRequestDTO
import org.grayrat.powerscheduler.common.dto.request.WorkerUnregisterRequestDTO
import org.grayrat.powerscheduler.common.enums.JobStatusEnum
import org.grayrat.powerscheduler.server.application.assembler.WorkerRegistryAssembler
import org.grayrat.powerscheduler.server.application.dto.response.WorkerQueryResponseDTO
import org.grayrat.powerscheduler.server.application.exception.BizException
import org.grayrat.powerscheduler.server.domain.appgroup.AppGroupRepository
import org.grayrat.powerscheduler.server.domain.jobinstance.JobInstanceRepository
import org.grayrat.powerscheduler.server.domain.workerregistry.WorkerRegistry
import org.grayrat.powerscheduler.server.domain.workerregistry.WorkerRegistryRepository
import org.grayrat.powerscheduler.server.domain.workerregistry.WorkerRegistryUniqueKey
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDateTime

/**
 * worker生命周期管理服务
 *
 * @author grayrat
 * @since 2025/5/21
 */
@Service
class WorkerLifeCycleService(
    private val appGroupRepository: AppGroupRepository,
    private val jobInstanceRepository: JobInstanceRepository,
    private val workerRegistryRepository: WorkerRegistryRepository,
    private val workerRegistryAssembler: WorkerRegistryAssembler,
    private val transactionTemplate: TransactionTemplate,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun list(appCode: String): List<WorkerQueryResponseDTO> {
        val workerRegistries = workerRegistryRepository.findAllByAppCode(appCode)
        return workerRegistries.map { workerRegistryAssembler.toWorkerQueryResponseDTO(it) }
    }

    fun register(param: WorkerRegisterRequestDTO, remoteHost: String): String {
        checkAppCertificate(appCode = param.appCode!!, appSecret = param.appSecret!!)
        return transactionTemplate.execute {
            val existWorkerRegistry = workerRegistryRepository.findByUk(
                WorkerRegistryUniqueKey(
                    appCode = param.appCode!!,
                    host = param.host?.takeIf { it.isNotBlank() } ?: remoteHost,
                    port = param.port!!,
                )
            )
            val accessToken = if (existWorkerRegistry != null) {
                existWorkerRegistry.lastHeartbeatAt = LocalDateTime.now()
                workerRegistryRepository.save(existWorkerRegistry)
                existWorkerRegistry.accessToken
            } else {
                val workerRegistryToSave = workerRegistryAssembler.toDomainModel4RegisterRequestDTO(param, remoteHost)
                workerRegistryRepository.save(workerRegistryToSave)
                workerRegistryToSave.accessToken
            }
            return@execute accessToken
        }!!
    }

    fun handleHeartbeat(param: WorkerHeartbeatRequestDTO, remoteAddr: String) {
        val existWorkerRegistry = workerRegistryRepository.findByUk(
            WorkerRegistryUniqueKey(
                appCode = param.appCode!!,
                host = param.host ?: remoteAddr,
                port = param.port!!,
            )
        )
        if (existWorkerRegistry != null) {
            if (existWorkerRegistry.accessToken != param.accessToken) {
                throw BizException(message = "handle heartbeat failed: invalid accessToken for appCode [${param.appCode}]")
            }
            existWorkerRegistry.lastHeartbeatAt = LocalDateTime.now()
            workerRegistryRepository.save(existWorkerRegistry)
        } else {
            throw BizException("handle heartbeat failed: worker has not registered")
        }
    }

    fun unregister(param: WorkerUnregisterRequestDTO, remoteAddr: String) {
        val uk = WorkerRegistryUniqueKey(
            appCode = param.appCode!!,
            host = param.host ?: remoteAddr,
            port = param.port!!,
        )
        val existWorkerRegistry = workerRegistryRepository.findByUk(uk)
        if (existWorkerRegistry != null) {
            if (existWorkerRegistry.accessToken != param.accessToken) {
                throw BizException(message = "unregister failed: invalid accessToken for worker [${existWorkerRegistry.address}]")
            }
            removeWorkerRegistry(existWorkerRegistry)
            log.info("unregister worker [{}] successful", existWorkerRegistry.address)
        } else {
            log.warn("worker [{}:{}] has not registered", uk.host, uk.port)
        }
    }

    fun removeWorkerRegistry(workerRegistry: WorkerRegistry) {
        transactionTemplate.executeWithoutResult {
            val existWorkerRegistry = workerRegistryRepository.lockById(workerRegistry.id!!)
            if (existWorkerRegistry == null) {
                return@executeWithoutResult
            }
            val workerAddress = existWorkerRegistry.address
            val uncompletedJobInstanceList = jobInstanceRepository.findAllUncompletedByWorkerAddress(workerAddress)
            uncompletedJobInstanceList.forEach {
                if (it.attemptCnt!! >= it.maxAttemptCnt!!) {
                    it.jobStatus = JobStatusEnum.FAILED
                    it.startAt = it.startAt ?: LocalDateTime.now()
                    it.endAt = LocalDateTime.now()
                    it.message = "worker is offline"
                } else {
                    it.attemptCnt = it.attemptCnt!! + 1
                    it.jobStatus = JobStatusEnum.WAITING_DISPATCH
                    it.startAt = null
                    it.endAt = null
                }
            }
            workerRegistryRepository.delete(existWorkerRegistry.id!!)
            jobInstanceRepository.saveAll(uncompletedJobInstanceList)
        }
    }

    fun checkAppCertificate(appCode: String, appSecret: String) {
        val appGroup = appGroupRepository.findByCode(appCode)
            ?: throw BizException(message = "appCode is invalid")
        if (appSecret != appGroup.secret) {
            throw BizException(message = "Invalid appCode or appSecret")
        }
    }

}