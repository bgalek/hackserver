package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import com.codahale.metrics.Gauge
import com.codahale.metrics.MetricRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.MeasurementsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.JsonConverter
import pl.allegro.experiments.chi.chiserver.infrastructure.druid.DruidClient

@Configuration
class ExperimentsConfig {
    private val EXPERIMENTS_COUNT_METRIC = "experiments.count";

    @Bean
    fun experimentsRepository(@Value("\${chi.experiments.file}") jsonUrl: String,
                              restTemplate: RestTemplate, jsonConverter: JsonConverter,
                              metricRegistry: MetricRegistry): ExperimentsRepository {
        val httpContentLoader = HttpContentLoader(restTemplate)
        val repo = FileBasedExperimentsRepository(jsonUrl, httpContentLoader::loadFromHttp, jsonConverter)

        val gauge = Gauge<Int> { repo.all.size }
        metricRegistry.register(EXPERIMENTS_COUNT_METRIC, gauge)

        return repo
    }

    @Bean
    fun measurementsRepository(druid: DruidClient, jsonConverter: JsonConverter,
                               @Value("\${druid.experimentsCube}") datasource: String): MeasurementsRepository
        = DruidMeasurementsRepository(druid, jsonConverter, datasource)


    @Bean
    fun refresher(repository: ExperimentsRepository): ExperimentRepositoryRefresher {
        return ExperimentRepositoryRefresher(repository)
    }
}
