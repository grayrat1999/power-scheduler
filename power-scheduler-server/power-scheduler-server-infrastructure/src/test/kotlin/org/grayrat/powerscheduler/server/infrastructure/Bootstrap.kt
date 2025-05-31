package org.grayrat.powerscheduler.server.infrastructure

import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * @author grayrat
 * @since 2025/4/17
 */
@SpringBootApplication(
    scanBasePackages = [
        "org.grayrat.powerscheduler.server.infrastructure",
        "org.grayrat.powerscheduler.server.domain",
    ]
)
class Bootstrap