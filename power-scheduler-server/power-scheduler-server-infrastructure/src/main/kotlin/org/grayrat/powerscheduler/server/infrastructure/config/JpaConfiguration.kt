package org.grayrat.powerscheduler.server.infrastructure.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * @author grayrat
 * @since 2025/4/16
 */
@Configuration
@EntityScan(basePackages = ["org.grayrat.powerscheduler.server.infrastructure.persistence.model"])
@EnableJpaRepositories(basePackages = ["org.grayrat.powerscheduler.server.infrastructure.persistence.repository.impl"])
class JpaConfiguration