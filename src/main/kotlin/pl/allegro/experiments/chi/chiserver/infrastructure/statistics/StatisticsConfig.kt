package pl.allegro.experiments.chi.chiserver.infrastructure.statistics

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.infrastructure.JsonConverter
import pl.allegro.experiments.chi.chiserver.infrastructure.statistics.DruidStatisticsRepository

@Configuration
class StatisticsConfig {

    @Bean
    fun statisticsRepository(@Value("\${druid.apiHost}") apiHost: String,
                             restTemplate: RestTemplate, jsonConverter: JsonConverter,
                             @Value("\${druid.datasource}") datasource: String) =
            DruidStatisticsRepository(apiHost, datasource, restTemplate, jsonConverter)
}