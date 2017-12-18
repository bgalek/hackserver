package pl.allegro.experiments.chi.chiserver.interactions

import com.codahale.metrics.MetricRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.allegro.experiments.chi.chiserver.domain.ExperimentsRepository

@Configuration
class InteractionsConfig {
    @Bean
    fun interactionConverter(): InteractionConverter {
        return InteractionConverter()
    }

    @Bean
    fun interactionFactory(
            experimentsRepository: ExperimentsRepository,
            interactionConverter: InteractionConverter) : InteractionsFactory {
        return InteractionsFactory(experimentsRepository, interactionConverter)
    }

    @Bean
    fun interactionsMetricsReporter(metricRegistry : MetricRegistry) : InteractionsMetricsReporter {
        return InteractionsMetricsReporter(metricRegistry)
    }
}