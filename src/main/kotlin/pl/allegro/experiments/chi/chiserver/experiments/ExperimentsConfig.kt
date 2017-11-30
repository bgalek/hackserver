package pl.allegro.experiments.chi.chiserver.experiments

import com.codahale.metrics.Gauge
import com.codahale.metrics.MetricRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.experiments.infrastructure.HttpContentLoader
import pl.allegro.experiments.chi.core.ExperimentsRepository
import pl.allegro.experiments.chi.persistence.FileBasedExperimentsRepository
import javax.annotation.PostConstruct

@Configuration
class ExperimentsConfig {
    private val EXPERIMENTS_COUNT_METRIC = "experiments.count";

    @Autowired
    lateinit var metricRegistry: MetricRegistry

    @Autowired
    lateinit var experimentsRepository: ExperimentsRepository

    @Bean
    fun experimentsRepository(@Value("\${chi.experiments.file}") jsonUrl: String,
                              restTemplate: RestTemplate) : FileBasedExperimentsRepository {
        val httpContentLoader = HttpContentLoader(restTemplate)
        return FileBasedExperimentsRepository(jsonUrl, httpContentLoader::loadFromHttp)
    }

    @PostConstruct
    fun configureMetrics() {
        val gauge = Gauge<Int> { experimentsRepository.all.size }
        metricRegistry.register(EXPERIMENTS_COUNT_METRIC, gauge)
    }
}