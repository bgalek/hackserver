package pl.allegro.experiments.chi.chiserver.infrastructure.statistics;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsMongoMetricsReporter;

@Configuration
class ClassicStatisticsConfig {

    @Bean
    ClassicStatisticsRepository statisticsRepository(MongoTemplate mongoTemplate,
                                                     ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter) {
        return new ClassicStatisticsRepository(new MongoClassicStatisticsForVariantMetricRepository(mongoTemplate, experimentsMongoMetricsReporter));
    }
}
