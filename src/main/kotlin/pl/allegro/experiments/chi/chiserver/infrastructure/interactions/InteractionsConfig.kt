package pl.allegro.experiments.chi.chiserver.infrastructure.interactions

import com.codahale.metrics.MetricRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.allegro.experiments.chi.chiserver.domain.interactions.InteractionsConverter

@Configuration
class InteractionsConfig {
    @Bean
    fun interactionConverter() : InteractionsConverter {
        return InteractionsConverter()
    }

    @Bean
    fun interactionsMetricsReporter(metricRegistry : MetricRegistry) : InteractionsMetricsReporter {
        return InteractionsMetricsReporter(metricRegistry)
    }
}