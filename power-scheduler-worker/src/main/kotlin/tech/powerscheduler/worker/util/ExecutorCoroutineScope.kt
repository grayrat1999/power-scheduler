package tech.powerscheduler.worker.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * 自定义的协程作用领，用于实现结构化并发
 *
 * @author grayrat
 * @since 2025/5/25
 */
internal object ExecutorCoroutineScope : CoroutineScope {

    /**
     * 用于管理所有协程的生命周期
     */
    private val manager = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + manager

    /**
     * 取消所有协程任务
     */
    fun cancelJobs() {
        manager.cancel()
    }
}