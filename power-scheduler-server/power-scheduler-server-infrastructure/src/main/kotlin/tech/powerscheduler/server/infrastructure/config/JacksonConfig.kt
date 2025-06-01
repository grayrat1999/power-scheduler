package tech.powerscheduler.server.infrastructure.config

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Configuration
class JacksonConfig {

    @Bean
    fun customizer(): Jackson2ObjectMapperBuilderCustomizer {
        val javaTimeModule = JavaTimeModule().apply {
            addSerializer(
                LocalDateTime::class.java,
                LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            )
            addDeserializer(
                LocalDateTime::class.java,
                LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            )
        }
        return Jackson2ObjectMapperBuilderCustomizer { builder ->
            builder.featuresToDisable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
            )
            builder.modules(javaTimeModule)
        }
    }

}