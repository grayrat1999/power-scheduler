package tech.powerscheduler.server.infrastructure.config

import org.springframework.context.annotation.Configuration
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableAsync

/**
 * @author grayrat
 * @since 2025/6/16
 */
@EnableAsync
@EnableRetry
@Configuration
class AppConfiguration {

}