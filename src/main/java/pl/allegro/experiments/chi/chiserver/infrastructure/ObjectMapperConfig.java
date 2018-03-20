package pl.allegro.experiments.chi.chiserver.infrastructure;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.avro.AvroMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import pl.allegro.tech.common.andamio.errors.jackson.ErrorsModule;

@Configuration
class ObjectMapperConfig {

    @Autowired
    void configureObjectMapper(ObjectMapper objectMapper) {
        applyCommonConfiguration(objectMapper);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        objectMapper.registerModule(ErrorsModule.module()).setVisibility(PropertyAccessor.FIELD,
                JsonAutoDetect.Visibility.PUBLIC_ONLY);
        objectMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Autowired
    void configureAvroMapper(AvroMapper avroMapper) {
        applyCommonConfiguration(avroMapper);
        avroMapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        avroMapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        avroMapper.disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
    }

    private void applyCommonConfiguration(ObjectMapper objectMapper) {
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new ParameterNamesModule());
        objectMapper.registerModule(new KotlinModule());
        objectMapper.registerModule(new AfterburnerModule());
    }
}
