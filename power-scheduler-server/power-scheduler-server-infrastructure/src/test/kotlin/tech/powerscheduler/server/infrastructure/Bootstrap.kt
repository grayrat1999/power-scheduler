package tech.powerscheduler.server.infrastructure

import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * @author grayrat
 * @since 2025/4/17
 */
@SpringBootApplication(
    scanBasePackages = [
        "tech.powerscheduler.server.infrastructure",
        "tech.powerscheduler.server.domain",
    ]
)
class Bootstrap