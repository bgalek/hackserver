package pl.allegro.tech.leaders.hackathon.registration.infrastructure.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.allegro.tech.leaders.hackathon.registration.api.HealthCheckClient;

@Configuration
class HealthCheckClientConfiguration {

    @Bean
    HealthCheckClient healthCheckClient() {
        return new HttpHealthCheckClient();
    }
}
