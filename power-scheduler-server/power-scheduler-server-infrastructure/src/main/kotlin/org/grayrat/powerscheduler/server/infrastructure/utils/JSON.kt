package org.grayrat.powerscheduler.server.infrastructure.utils

import com.fasterxml.jackson.databind.ObjectMapper

object JSON {

    fun writeValueAsString(o: Any): String {
        return ObjectMapper().writeValueAsString(o)
    }

}