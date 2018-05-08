package pl.allegro.experiments.chi.chiserver.infrastructure.statistics;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import pl.allegro.experiments.chi.chiserver.domain.statistics.BayesianStatisticsRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.druid.DruidClient;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsMongoMetricsReporter;


@Configuration
class StatisticsConfig {

    @Bean
    DruidStatisticsRepository statisticsRepository(
            DruidClient druid,
            Gson jsonConverter,
            @Value("${druid.experimentsStatsCube}") String datasource) {
        return new DruidStatisticsRepository(druid, datasource, jsonConverter);
    }

    @Bean
    BayesianStatisticsRepository bayesianStatisticsRepository(MongoTemplate mongoTemplate,
                                                              ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter) {
        return new MongoBayesianStatisticsRepository(mongoTemplate, experimentsMongoMetricsReporter);
    }
}
