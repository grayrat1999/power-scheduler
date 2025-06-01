package tech.powerscheduler.worker.processor

/**
 * 任务处理结果
 *
 * @author grayrat
 * @since 2025/4/26
 */
sealed class ProcessResult {

    /**
     * 任务处理成功的结果
     *
     * @author grayrat
     * @since 2025/4/26
     */
    class Success() : ProcessResult()

    /**
     * 任务处理失败的结果
     *
     * @property message 错误信息
     */
    class Failure(
        val message: String? = null,
    ) : ProcessResult()

    companion object {
        /**
         * 用于创建成功结果的工厂方法
         */
        @JvmStatic
        fun success() = Success()

        /**
         * 用于创建失败结果的工厂方法
         */
        @JvmStatic
        fun failure(errorMessage: String?) = Failure(errorMessage)
    }
}
