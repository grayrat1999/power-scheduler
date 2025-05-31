package org.grayrat.powerscheduler.server.interfaces.controller

import org.grayrat.powerscheduler.common.dto.response.ResponseWrapper

/**
 * @author grayrat
 * @since 2025/5/29
 */
open class BaseController {

    internal fun <T> wrapperResponse(block: () -> T?): ResponseWrapper<T> {
        val result = block()
        return success(result)
    }

    internal fun <T> success(data: T?) = ResponseWrapper(
        data = data,
        success = true,
        code = "0",
        message = "success",
    )

}