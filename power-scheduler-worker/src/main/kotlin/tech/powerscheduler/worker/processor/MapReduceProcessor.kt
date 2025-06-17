package tech.powerscheduler.worker.processor

import tech.powerscheduler.common.enums.TaskTypeEnum
import tech.powerscheduler.worker.task.MapTaskContext
import tech.powerscheduler.worker.task.ReduceTaskContext
import tech.powerscheduler.worker.task.TaskContext

/**
 * MapReduce任务处理器基类
 *
 * @author grayrat
 * @since 2025/6/17
 */
abstract class MapReduceProcessor : MapProcessor() {

    override fun process(context: TaskContext): ProcessResult? {
        return if (isReduceTask(context)) {
            reduce(context as ReduceTaskContext)
        } else {
            process(context as MapTaskContext)
        }
    }

    @Throws(Exception::class)
    abstract fun reduce(context: ReduceTaskContext): ProcessResult?

    private fun isReduceTask(context: TaskContext): Boolean {
        return context is ReduceTaskContext && context.taskType == TaskTypeEnum.REDUCE
    }
}