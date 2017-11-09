package pl.allegro.experiments.chi.chiserver.experiments

import com.codahale.metrics.Gauge
import com.codahale.metrics.MetricRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.experiments.infrastructure.HttpContentLoader
import pl.allegro.experiments.chi.chiserver.experiments.v1.JsonConverter
import pl.allegro.experiments.chi.core.ExperimentsRepository
import pl.allegro.experiments.chi.persistence.FileBasedExperimentsRepository
import pl.allegro.tech.common.andamio.spring.client.ClientConnectionConfig
import pl.allegro.tech.common.andamio.spring.client.RestTemplateFactory
import javax.annotation.PostConstruct

@Configuration
class ExperimentsConfig {
    private val EXPERIMENTS_COUNT_METRIC = "experiments.count";

    @Autowired
    lateinit var metricRegistry: MetricRegistry

    @Autowired
    lateinit var experimentsRepository: ExperimentsRepository

    @Bean
    fun restTemplate(factory: RestTemplateFactory): RestTemplate {
        val connectionConfig = ClientConnectionConfig.clientConnectionConfig()
                .withMaxConnections(1)
                .withMaxConnectionsPerRoute(1)
                .withSocketTimeout(300)
                .withConnectionTimeout(300)
                .build()
        return factory.usingApacheHttp().create(connectionConfig)
    }

    @Bean
    fun jsonConverter(): JsonConverter = JsonConverter()

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