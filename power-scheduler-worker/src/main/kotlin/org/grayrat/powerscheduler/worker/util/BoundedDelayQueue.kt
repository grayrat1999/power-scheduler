package org.grayrat.powerscheduler.worker.util

import java.util.concurrent.DelayQueue
import java.util.concurrent.Delayed

/**
 * @author grayrat
 * @since 2025/5/15
 */
class BoundedDelayQueue<E : Delayed>(
    private val capacity: Int
) {

    private val delayQueue: DelayQueue<E> = DelayQueue<E>()

    private val size
        get() = delayQueue.size

    @Synchronized
    fun offer(e: E): Boolean {
        if (size == capacity) {
            return false
        }
        return delayQueue.offer(e)
    }

    @Synchronized
    fun poll(): E? {
        val item = delayQueue.poll()
        return item
    }
}

