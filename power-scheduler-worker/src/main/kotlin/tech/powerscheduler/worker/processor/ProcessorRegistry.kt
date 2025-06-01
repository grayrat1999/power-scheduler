package tech.powerscheduler.worker.processor

import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * 任务处理器注册表
 *
 * @author grayrat
 * @since 2025/4/26
 */
object ProcessorRegistry {

    private val log = LoggerFactory.getLogger(ProcessorRegistry::class.java)
    private val REGISTRY: MutableMap<String, Processor> = ConcurrentHashMap<String, Processor>()

    init {
        register(ScriptProcessor())
    }

    /**
     * 注册任务处理器
     *
     * @param processor 任务处理器
     */
    @JvmStatic
    fun register(processor: Processor) {
        if (processor.path.isNullOrBlank()) {
            log.error("register ${processor::class.simpleName} failed, path cannot be null or blank!")
        } else {
            REGISTRY.put(processor.path!!, processor)
        }
    }

    /**
     * 根据路径查找已经注册的任务处理器
     *
     * @param processorPath 任务处理器路径
     * @return 如果存在返回任务处理器, 否则返回null
     */
    @JvmStatic
    fun find(processorPath: String?): Processor? {
        return REGISTRY[processorPath]
    }

}
