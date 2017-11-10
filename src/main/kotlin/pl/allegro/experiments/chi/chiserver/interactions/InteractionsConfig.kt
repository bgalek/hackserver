package pl.allegro.experiments.chi.chiserver.interactions

import com.codahale.metrics.MetricRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

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