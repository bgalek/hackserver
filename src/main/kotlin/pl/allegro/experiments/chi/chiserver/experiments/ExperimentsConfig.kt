package pl.allegro.experiments.chi.chiserver.experiments

import com.codahale.metrics.Gauge
import com.codahale.metrics.MetricRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.domain.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.experiments.infrastructure.HttpContentLoader
import pl.allegro.experiments.chi.chiserver.infrastructure.FileBasedExperimentsRepository

@Configuration
class ExperimentsConfig {
    private val EXPERIMENTS_COUNT_METRIC = "experiments.count";

    @Bean
    fun experimentsRepository(@Value("\${chi.experiments.file}") jsonUrl: String,
                              restTemplate: RestTemplate,
                              metricRegistry: MetricRegistry): ExperimentsRepository {
        val httpContentLoader = HttpContentLoader(restTemplate)
        val repo = FileBasedExperimentsRepository(jsonUrl, httpContentLoader::loadFromHttp)

        val gauge = Gauge<Int> { repo.all.size }
        metricRegistry.register(EXPERIMENTS_COUNT_METRIC, gauge)

        return repo
    }
}
