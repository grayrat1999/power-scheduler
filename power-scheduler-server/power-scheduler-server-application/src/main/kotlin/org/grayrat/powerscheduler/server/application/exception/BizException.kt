package org.grayrat.powerscheduler.server.application.exception

/**
 * 业务异常
 *
 * @author grayrat
 * @since 2025/5/29
 */
class BizException(
    /**
     * 异常信息
     */
    message: String?,
    /**
     * 异常根源
     */
    cause: Throwable? = null,
) : RuntimeException(message, cause)