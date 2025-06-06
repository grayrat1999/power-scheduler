package tech.powerscheduler.worker.autoconfigure

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tech.powerscheduler.worker.PowerSchedulerWorker
import tech.powerscheduler.worker.PowerSchedulerWorkerProperties

/**
 * PowerSchedulerWorker的自动装配类
 *
 * @author grayrat
 * @since 2025/5/17
 */
@Configuration
@ConditionalOnClass(PowerSchedulerWorker::class)
@EnableConfigurationProperties(PowerSchedulerWorkerProperties::class)
@ConditionalOnProperty(prefix = "power-scheduler", name = ["enabled"], havingValue = "true", matchIfMissing = true)
open class PowerSchedulerWorkerAutoConfiguration {

    @Bean(initMethod = "init", destroyMethod = "destroy")
    open fun powerSchedulerClient(properties: PowerSchedulerWorkerProperties): PowerSchedulerWorker {
        return PowerSchedulerWorker(properties)
    }

    companion object {
        @Bean
        @JvmStatic
        fun processorDetector(): PowerSchedulerProcessorDetector {
            return PowerSchedulerProcessorDetector()
        }
    }
}