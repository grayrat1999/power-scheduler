package tech.powerscheduler.server.domain.worker

import tech.powerscheduler.common.dto.request.JobDispatchRequestDTO
import tech.powerscheduler.common.dto.request.JobTerminateRequestDTO
import tech.powerscheduler.common.dto.response.ResponseWrapper

/**
 * Worker的远程接口调用服务
 *
 * @author grayrat
 * @since 2025/5/28
 */
interface WorkerRemoteService {

    fun dispatch(baseUrl: String, param: JobDispatchRequestDTO): ResponseWrapper<Boolean>

    fun terminate(baseUrl: String, param: JobTerminateRequestDTO): ResponseWrapper<Boolean>

}