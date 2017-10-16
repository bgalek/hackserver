package pl.allegro.experiments.chi.chiserver.infrastructure

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.avro.AvroMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.allegro.tech.common.andamio.errors.jackson.ErrorsModule

@Configuration
class ObjectMapperConfig {

    @Bean
    fun errorsModule(): ErrorsModule = ErrorsModule.module()

    @Bean
    fun kotlinModule(): KotlinModule = KotlinModule()

    @Autowired
    fun configureAvroMapper(avroMapper: AvroMapper) {
        avroMapper.apply {
            registerModule(Jdk8Module())
            registerModule(JavaTimeModule())
            registerModule(ParameterNamesModule())
            registerModule(KotlinModule())
            registerModule(AfterburnerModule())
            enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
        }
    }
}