package tech.powerscheduler.worker.task

import tech.powerscheduler.common.dto.response.PageDTO
import tech.powerscheduler.worker.task.TaskResultSet.Item

/**
 * @author grayrat
 * @since 2025/6/18
 */
class TaskResultSet(
    var resultSetProvider: (pageNo: Int) -> PageDTO<Item>
) : Iterable<Item> {

    override fun iterator(): Iterator<Item> {
        return ResultIterator()
    }

    class Item {
        /**
         * 子任务id
         */
        var taskId: Long? = null

        /**
         * 子任务名称
         */
        var taskName: String? = null

        /**
         * 子任务结果内容
         */
        var result: String? = null

        override fun toString(): String {
            return "Item(taskId=$taskId, taskName=$taskName, result=$result)"
        }
    }

    inner class ResultIterator : Iterator<Item> {
        var pageNo: Int = 1

        var hasNext: Boolean? = null

        var cursor: Int = 0

        var items: List<Item> = emptyList()

        override fun next(): Item {
            return items[cursor++]
        }

        override fun hasNext(): Boolean {
            if (hasNext != null && hasNext!!.not()) {
                return false
            }
            if (hasNext == null || cursor >= items.size) {
                val curPage = resultSetProvider.invoke(pageNo++)
                items = curPage.content
                hasNext = items.isNotEmpty()
                cursor = 0
            }
            return hasNext!!
        }
    }
}