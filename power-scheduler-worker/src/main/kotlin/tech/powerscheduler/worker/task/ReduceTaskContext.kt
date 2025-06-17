package tech.powerscheduler.worker.task

import tech.powerscheduler.common.dto.response.PageDTO
import tech.powerscheduler.worker.task.TaskResultSet.Item

/**
 * @author grayrat
 * @since 2025/6/18
 */
class ReduceTaskContext(
    private val resultSetProvider: (jobInstanceId: Long, pageNo: Int, pageSize: Int) -> PageDTO<Item>
) : TaskContext() {

    var fetchResultBatchSize: Int = 100

    val taskResultSet: TaskResultSet
        get() = TaskResultSet(
            resultSetProvider = { pageNo: Int -> resultSetProvider(jobInstanceId!!, pageNo, fetchResultBatchSize) }
        )

}