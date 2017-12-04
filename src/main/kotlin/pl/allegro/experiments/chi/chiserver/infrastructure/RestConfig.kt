package pl.allegro.experiments.chi.chiserver.infrastructure

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import pl.allegro.tech.common.andamio.spring.client.OkHttpClientConfig
import pl.allegro.tech.common.andamio.spring.client.RestTemplateFactory


@Configuration
class RestConfig {
    @Bean
    fun restTemplate(factory: RestTemplateFactory): RestTemplate {
        val connectionConfig = OkHttpClientConfig.okHttpClientConfig()
                .withMaxIdleConnections(2)
                .withReadTimeoutMillis(300)
                .withWriteTimeoutMillis(300)
                .withConnectionTimeoutMillis(300)
                // https://jira.allegrogroup.com/browse/SKYLAB-2072
                .withKeepAliveDurationMillis(500)
                .withRetryOnConnectionFailure(true)
                .build()
        return factory.usingOkHttp().create(connectionConfig)
    }
}
