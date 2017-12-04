package pl.allegro.experiments.chi.chiserver.infrastructure

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class StatisticsConfig {

    @Bean
    fun statisticsRepository(@Value("\${druid.apiHost}") apiHost: String,
                             restTemplate: RestTemplate, jsonConverter: JsonConverter) =
            DruidStatisticsRepository(apiHost, restTemplate, jsonConverter)
}