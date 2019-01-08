package pl.allegro.tech.leaders.hackathon.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ObjectMapperConfiguration {
    @Bean
    ObjectMapper objectMapper() {
        return ObjectMapperProvider.objectMapper();
    }
}
