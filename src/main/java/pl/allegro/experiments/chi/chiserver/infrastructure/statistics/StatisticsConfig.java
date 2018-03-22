package pl.allegro.experiments.chi.chiserver.infrastructure.statistics;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.allegro.experiments.chi.chiserver.infrastructure.druid.DruidClient;


@Configuration
class StatisticsConfig {

    @Bean
    DruidStatisticsRepository statisticsRepository(
            DruidClient druid,
            Gson jsonConverter,
            @Value("${druid.experimentsStatsCube}") String datasource) {
        return new DruidStatisticsRepository(druid, datasource, jsonConverter);
    }
}
