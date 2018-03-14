package pl.allegro.experiments.chi.chiserver.infrastructure.druid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DruidConfig {

    @Bean
    DruidClient druid(@Value("${druid.apiHost}") String apiHost, RestTemplate restTemplate) {
        return new DruidClient(apiHost, restTemplate);
    }
}
