package org.grayrat.powerscheduler.worker.processor

import org.grayrat.powerscheduler.worker.job.JobContext

/**
 * 任务处理器接口
 *
 * @author grayrat
 * @since 2025/4/26
 */
interface Processor {

    /**
     * 处理任务
     *
     * @param context 任务上下文
     * @return 任务执行结果
     */
    @Throws(Exception::class)
    fun process(context: JobContext): ProcessResult?

    /**
     * 处理器路径
     */
    val path: String?
}
