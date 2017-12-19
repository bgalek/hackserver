package pl.allegro.experiments.chi.chiserver.infrastructure.interactions

import com.codahale.metrics.MetricRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.interactions.InteractionConverter
import pl.allegro.experiments.chi.chiserver.domain.interactions.InteractionsFactory

@Configuration
class InteractionsConfig {
    @Bean
    fun interactionConverter(): InteractionConverter {
        return InteractionConverter()
    }

    @Bean
    fun interactionFactory(
            interactionsMetricsReporter: InteractionsMetricsReporter,
            experimentsRepository: ExperimentsRepository,
            interactionConverter: InteractionConverter) : InteractionsFactory {
        return InteractionsFactory(interactionsMetricsReporter, experimentsRepository, interactionConverter)
    }

    @Bean
    fun interactionsMetricsReporter(metricRegistry : MetricRegistry) : InteractionsMetricsReporter {
        return InteractionsMetricsReporter(metricRegistry)
    }
}