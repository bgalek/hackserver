package pl.allegro.experiments.chi.chiserver

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import pl.allegro.tech.common.andamio.spring.client.RestTemplateFactory
import spock.lang.Specification

import static pl.allegro.tech.common.andamio.spring.client.ClientConnectionConfig.clientConnectionConfig

@SpringBootTest(classes = [AppRunner, RestTemplateConfig],
        properties = "application.environment=integration",
        webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
class BaseIntegrationTest extends Specification {

    @Value('${local.server.port}')
    protected int port

    protected String localUrl(String endpoint) {
        return "http://localhost:$port$endpoint"
    }

    @Configuration
    static class RestTemplateConfig {

        /**
         * Timeout has to be increased because of Hystrix warm-up
         */
        @Bean
        @Primary
        RestTemplate restTemplate(RestTemplateFactory factory) {
            return factory.usingApacheHttp().create(clientConnectionConfig()
                    .withSocketTimeout(1000).build())
        }
    }
}
