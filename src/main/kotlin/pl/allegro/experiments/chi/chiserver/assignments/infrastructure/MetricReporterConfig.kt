package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import com.codahale.metrics.MetricRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MetricReporterConfig {
    @Bean
    fun metricReporter(metricRegistry: MetricRegistry): MetricReporter {
        return MetricReporter(metricRegistry)
    }
}