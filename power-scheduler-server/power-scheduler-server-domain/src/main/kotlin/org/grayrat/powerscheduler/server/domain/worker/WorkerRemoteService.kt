package org.grayrat.powerscheduler.server.domain.worker

import org.grayrat.powerscheduler.common.dto.request.JobDispatchRequestDTO
import org.grayrat.powerscheduler.common.dto.request.JobTerminateRequestDTO
import org.grayrat.powerscheduler.common.dto.response.ResponseWrapper

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