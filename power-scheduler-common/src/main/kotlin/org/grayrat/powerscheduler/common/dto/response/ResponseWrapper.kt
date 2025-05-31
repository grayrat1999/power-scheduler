package org.grayrat.powerscheduler.common.dto.response

/**
 * 接口响应保证类
 *
 * @author grayrat
 * @since 2025/5/14
 */
class ResponseWrapper<T>(
    /**
     * 响应结果
     */
    var data: T?,
    /**
     * 是否成功
     */
    var success: Boolean,
    /**
     * 错误码
     */
    var code: String,
    /**
     * 错误信息
     */
    var message: String,
    /**
     * 异常堆栈
     */
    var cause: Throwable? = null,
) {
    constructor() : this(
        data = null,
        success = false,
        code = "200",
        message = "success"
    )
}