package tech.powerscheduler.worker.exception

/**
 * PowerScheduler的worker异常
 *
 * @author grayrat
 * @since 2025/5/23
 */
class PowerSchedulerWorkerException(
    /**
     * 异常信息
     */
    message: String,
    /**
     * 异常
     */
    cause: Throwable? = null
) : RuntimeException(message, cause)
