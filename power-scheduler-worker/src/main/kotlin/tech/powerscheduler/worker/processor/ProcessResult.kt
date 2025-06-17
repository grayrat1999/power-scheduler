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
    class Success(
        val result: Any?,
    ) : ProcessResult()

    /**
     * 任务处理失败的结果
     *
     * @property message 错误信息
     */
    class Failure(
        val message: String? = null,
    ) : ProcessResult()

    /**
     * 任务Map的结果
     *
     * @property taskList 子任务列表
     * @property taskName 子任务名称
     */
    class Map(
        val taskList: List<Any>,
        val taskName: String,
    ) : ProcessResult()

    companion object {
        /**
         * 用于创建成功结果的工厂方法
         */
        @JvmStatic
        fun success() = Success(null)

        /**
         * 用于创建成功结果的工厂方法
         */
        @JvmStatic
        fun success(result: Any?) = Success(result)

        /**
         * 用于创建失败结果的工厂方法
         */
        @JvmStatic
        fun failure(errorMessage: String?) = Failure(errorMessage)
    }
}
