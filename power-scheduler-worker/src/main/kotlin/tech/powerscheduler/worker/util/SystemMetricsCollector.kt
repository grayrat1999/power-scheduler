package tech.powerscheduler.worker.util

import oshi.SystemInfo
import oshi.hardware.CentralProcessor

/**
 * 系统指标收集工具
 *
 * @author grayrat
 * @since 2025/6/16
 */
object SystemMetricsCollector {

    /**
     * 获取系统CPU使用率（0~1）
     */
    fun getSystemCpuLoad(): Double {
        val systemInfo = SystemInfo()
        val processor: CentralProcessor = systemInfo.hardware.processor
        val prevTicks: LongArray = processor.systemCpuLoadTicks
        Thread.sleep(1000)
        return processor.getSystemCpuLoadBetweenTicks(prevTicks)
    }

    /**
     * 获取JVM堆内存利用率（0~1）
     */
    fun getJvmMemoryUsage(): Double {
        val runtime = Runtime.getRuntime()
        val maxMemory = runtime.maxMemory().toDouble()
        val usedMemory = (runtime.totalMemory() - runtime.freeMemory()).toDouble()
        return usedMemory / maxMemory
    }
}