package pl.allegro.experiments.chi.chiserver.infrastructure.druid

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class DruidConfig {

    @Bean
    fun druid(@Value("\${druid.apiHost}") apiHost: String, restTemplate: RestTemplate) =
        DruidClient(apiHost, restTemplate)
}