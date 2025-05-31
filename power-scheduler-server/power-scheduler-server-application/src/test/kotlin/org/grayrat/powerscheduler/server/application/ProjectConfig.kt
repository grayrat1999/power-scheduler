package org.grayrat.powerscheduler.server.application

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.extensions.htmlreporter.HtmlReporter
import io.kotest.extensions.junitxml.JunitXmlReporter

/**
 * @author grayrat
 * @since 2025/4/21
 */
open class ProjectConfig : AbstractProjectConfig() {

    override val specExecutionOrder = SpecExecutionOrder.Annotated

    override fun extensions(): List<Extension> = listOf(
        JunitXmlReporter(
            includeContainers = false,
            useTestPathAsName = true,
        ),
        HtmlReporter()
    )

}