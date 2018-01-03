package pl.allegro.experiments.chi.chiserver.infrastructure.statistics

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.allegro.experiments.chi.chiserver.infrastructure.JsonConverter
import pl.allegro.experiments.chi.chiserver.infrastructure.druid.DruidClient

@Configuration
class StatisticsConfig {

    @Bean
    fun statisticsRepository(druid: DruidClient, jsonConverter: JsonConverter,
                             @Value("\${druid.experimentsStatsCube}") datasource: String) =
        DruidStatisticsRepository(druid, datasource, jsonConverter)
}