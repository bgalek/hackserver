package pl.allegro.experiments.chi.chiserver.infrastructure.statistics;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration
class DruidConfig {

    @Bean
    DruidClient druid(@Value("${druid.apiHost}") String apiHost, RestTemplate restTemplate) {
        return new DruidClient(apiHost, restTemplate);
    }

    @Bean
    DruidStatisticsRepository statisticsRepository(
            DruidClient druid,
            Gson jsonConverter,
            @Value("${druid.experimentsStatsCube}") String datasource) {
        return new DruidStatisticsRepository(druid, datasource, jsonConverter);
    }
}
