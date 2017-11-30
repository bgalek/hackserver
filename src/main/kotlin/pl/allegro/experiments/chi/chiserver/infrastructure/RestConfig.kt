package pl.allegro.experiments.chi.chiserver.infrastructure

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import pl.allegro.tech.common.andamio.spring.client.ClientConnectionConfig
import pl.allegro.tech.common.andamio.spring.client.RestTemplateFactory

@Configuration
class RestConfig {
    @Bean
    fun restTemplate(factory: RestTemplateFactory): RestTemplate {
        val connectionConfig = ClientConnectionConfig.clientConnectionConfig()
                .withMaxConnections(1)
                .withMaxConnectionsPerRoute(1)
                .withSocketTimeout(300)
                .withConnectionTimeout(300)
                .build()
        return factory.usingApacheHttp().create(connectionConfig)
    }
}