package tech.powerscheduler.worker.autoconfigure

import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import tech.powerscheduler.worker.processor.Processor
import tech.powerscheduler.worker.processor.ProcessorRegistry.register

/**
 * 任务处理器自动侦测器，用于自动注册通过spring bean创建任务处理器
 *
 * @author grayrat
 * @since 2025/5/17
 */
@Component
class PowerSchedulerProcessorDetector : BeanPostProcessor {

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        if (bean is Processor) {
            register(bean)
        }
        return bean
    }
}
