package pl.allegro.experiments.chi.chiserver.infrastructure.interactions;

import com.codahale.metrics.MetricRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.interactions.InteractionConverter;
import pl.allegro.experiments.chi.chiserver.domain.interactions.InteractionsFactory;

@Configuration
class InteractionsConfig {

    @Bean
    InteractionConverter interactionConverter() {
        return new InteractionConverter();
    }

    @Bean
    InteractionsFactory interactionsFactory(
            InteractionsMetricsReporter interactionsMetricsReporter,
            ExperimentsRepository experimentsRepository,
            InteractionConverter interactionConverter) {
        return new InteractionsFactory(interactionsMetricsReporter, experimentsRepository, interactionConverter);
    }

    @Bean
    InteractionsMetricsReporter interactionsMetricsReporter(MetricRegistry metricRegistry) {
        return new InteractionsMetricsReporter(metricRegistry);
    }
}