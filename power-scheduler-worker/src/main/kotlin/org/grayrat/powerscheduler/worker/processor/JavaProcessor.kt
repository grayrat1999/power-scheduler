package org.grayrat.powerscheduler.worker.processor

/**
 * Java任务处理器
 *
 * @author grayrat
 * @since 2025/5/15
 */
abstract class JavaProcessor : Processor {
    override val path: String?
        get() = javaClass.getName()
}
