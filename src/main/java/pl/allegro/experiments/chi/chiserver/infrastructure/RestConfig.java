package pl.allegro.experiments.chi.chiserver.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import pl.allegro.tech.common.andamio.spring.client.OkHttpClientConfig;
import pl.allegro.tech.common.andamio.spring.client.RestTemplateFactory;

@Configuration
public class RestConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateFactory factory) {
        OkHttpClientConfig connectionConfig = OkHttpClientConfig.okHttpClientConfig()
                .withMaxIdleConnections(2)
                .withReadTimeoutMillis(2000)
                .withWriteTimeoutMillis(2000)
                .withConnectionTimeoutMillis(2000)
                .withKeepAliveDurationMillis(500)
                .withRetryOnConnectionFailure(true)
                .build();

        return factory.usingOkHttp().create(connectionConfig);
    }
}
