package tech.powerscheduler.worker.task

import tech.powerscheduler.worker.util.JsonUtil

/**
 * @author grayrat
 * @since 2025/6/17
 */
open class MapTaskContext(
    /**
     * 任务内容
     */
    val taskBody: String? = null,
) : TaskContext() {

    fun <T> getSubTask(clazz: Class<T>): T? {
        if (taskBody.isNullOrBlank()) {
            return null
        }
        return JsonUtil.readValue(taskBody, clazz)
    }

}