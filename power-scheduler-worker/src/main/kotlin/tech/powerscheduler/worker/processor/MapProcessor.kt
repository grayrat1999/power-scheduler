package tech.powerscheduler.worker.processor

import tech.powerscheduler.common.enums.TaskTypeEnum
import tech.powerscheduler.worker.task.MapTaskContext
import tech.powerscheduler.worker.task.TaskContext

/**
 * Map任务处理器基类
 *
 * @author grayrat
 * @since 2025/6/17
 */
abstract class MapProcessor : Processor {

    override val path: String?
        get() = javaClass.getName()

    override fun process(context: TaskContext): ProcessResult? {
        return process(context as MapTaskContext)
    }

    @Throws(Exception::class)
    abstract fun process(context: MapTaskContext): ProcessResult?

    fun isRootTask(context: MapTaskContext): Boolean {
        return context.taskType == TaskTypeEnum.ROOT
    }

    fun map(
        taskList: List<Any>,
        taskName: String,
    ): ProcessResult {
        return ProcessResult.Map(
            taskList = taskList,
            taskName = taskName,
        )
    }
}