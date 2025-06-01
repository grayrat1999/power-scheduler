package tech.powerscheduler.server.bootstrap

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * @author grayrat
 * @since 2025/4/13
 */
@SpringBootApplication(
    scanBasePackages = [
        "tech.powerscheduler.server.interfaces",
        "tech.powerscheduler.server.application",
        "tech.powerscheduler.server.infrastructure",
        "tech.powerscheduler.server.domain",
    ]
)
class PowerSchedulerServerApplication

fun main(args: Array<String>) {
    runApplication<PowerSchedulerServerApplication>(*args)
}
