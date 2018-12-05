package pl.allegro.experiments.chi.chiserver.infrastructure.statistics;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianStatisticsForVariantRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsMongoMetricsReporter;

@Configuration
public class BayesianStatisticsRepositoryConfig {

    @Bean
    MongoBayesianStatisticsForVariantRepository mongoBayesianStatisticsForVariantRepository(
            MongoTemplate mongoTemplate,
            ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter) {
        return new MongoBayesianStatisticsForVariantRepository(
                mongoTemplate,
                experimentsMongoMetricsReporter);
    }

    @Primary
    @Bean
    BayesianStatisticsForVariantRepository bayesianStatisticsForVariantRepository(
            MongoBayesianStatisticsForVariantRepository mongoBayesianStatisticsForVariantRepository,
            ExperimentsRepository experimentsRepository) {
        return new CachedBayesianStatisticsForVariantRepository(
                mongoBayesianStatisticsForVariantRepository,
                experimentsRepository);
    }
}
