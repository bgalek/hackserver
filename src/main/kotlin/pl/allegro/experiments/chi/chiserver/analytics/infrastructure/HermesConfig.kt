package pl.allegro.experiments.chi.chiserver.analytics.infrastructure

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.AsyncRestTemplate
import pl.allegro.experiments.chi.chiserver.infrastructure.ClientConnectionProperties
import pl.allegro.tech.common.andamio.spring.client.AsyncRestTemplateFactory
import pl.allegro.tech.hermes.client.HermesSender
import pl.allegro.tech.hermes.client.restTemplate.RestTemplateHermesSender

@Configuration
@EnableConfigurationProperties(HermesTopicProperties::class)
class HermesConfig {

    @Bean
    fun asyncRestTemplate(
            @Qualifier("hermesClientConnectionProperties") connectionProperties: ClientConnectionProperties,
            asyncRestTemplateFactory: AsyncRestTemplateFactory): AsyncRestTemplate {
        val connectionConfig = connectionProperties.toConfig()
        return asyncRestTemplateFactory.usingApacheHttp()
                .create(connectionConfig)
    }

    @Bean
    fun hermesSender(asyncRestTemplate: AsyncRestTemplate): HermesSender = RestTemplateHermesSender(asyncRestTemplate)

    @Bean
    @ConfigurationProperties(prefix = "hermes.client")
    fun hermesClientConnectionProperties(): ClientConnectionProperties = ClientConnectionProperties()
}