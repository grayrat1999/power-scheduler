package tech.powerscheduler.worker.util

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author grayrat
 * @since 2025/4/26
 */
class BasicThreadFactory(
    private val threadNamePrefix: String? = null
) : ThreadFactory {

    override fun newThread(runnable: Runnable): Thread {
        val thread = Thread(runnable, threadNamePrefix + THREAD_NUMBER.getAndIncrement())
        thread.setDaemon(false)
        thread.setPriority(Thread.NORM_PRIORITY)
        return thread
    }

    companion object {
        private val THREAD_NUMBER = AtomicInteger(1)
    }
}
