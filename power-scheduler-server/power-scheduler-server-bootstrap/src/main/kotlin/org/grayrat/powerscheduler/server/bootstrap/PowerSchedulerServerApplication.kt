package org.grayrat.powerscheduler.server.bootstrap

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * @author grayrat
 * @since 2025/4/13
 */
@SpringBootApplication(
    scanBasePackages = [
        "org.grayrat.powerscheduler.server.interfaces",
        "org.grayrat.powerscheduler.server.application",
        "org.grayrat.powerscheduler.server.infrastructure",
        "org.grayrat.powerscheduler.server.domain",
    ]
)
class PowerSchedulerServerApplication

fun main(args: Array<String>) {
    runApplication<PowerSchedulerServerApplication>(*args)
}
