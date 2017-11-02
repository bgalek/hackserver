package pl.allegro.experiments.chi.chiserver.infrastructure

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.avro.AvroMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import pl.allegro.tech.common.andamio.errors.jackson.ErrorsModule

@Configuration
class ObjectMapperConfig {

    @Autowired
    fun configureObjectMapper(objectMapper: ObjectMapper) {
        applyCommonConfiguration(objectMapper)
        objectMapper.apply {
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            registerModule(ErrorsModule.module()).setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.PUBLIC_ONLY)
            disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    @Autowired
    fun configureAvroMapper(avroMapper: AvroMapper) {
        applyCommonConfiguration(avroMapper)
        avroMapper.apply {
            enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
        }
    }

    private fun applyCommonConfiguration(objectMapper: ObjectMapper) {
        objectMapper.apply {
            registerModule(Jdk8Module())
            registerModule(JavaTimeModule())
            registerModule(ParameterNamesModule())
            registerModule(KotlinModule())
            registerModule(AfterburnerModule())
        }
    }
}