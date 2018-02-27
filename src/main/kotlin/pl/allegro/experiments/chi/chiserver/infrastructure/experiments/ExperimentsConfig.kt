package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import com.codahale.metrics.Gauge
import com.codahale.metrics.MetricRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.convert.CustomConversions
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.application.experiments.AllEnabledCrisisManagementFilter
import pl.allegro.experiments.chi.chiserver.application.experiments.CrisisManagementFilter
import pl.allegro.experiments.chi.chiserver.application.experiments.WhitelistCrisisManagementFilter
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.MeasurementsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.JsonConverter
import pl.allegro.experiments.chi.chiserver.infrastructure.druid.DruidClient

@Configuration
class ExperimentsConfig {
    private val EXPERIMENTS_COUNT_METRIC = "experiments.count";

    @Bean
    fun fileBasedExperimentsRepository(
        @Value("\${chi.experiments.file}") jsonUrl: String,
        restTemplate: RestTemplate, jsonConverter: JsonConverter,
        metricRegistry: MetricRegistry
    ): FileBasedExperimentsRepository {
        val httpContentLoader = HttpContentLoader(restTemplate)
        val repo = FileBasedExperimentsRepository(jsonUrl, httpContentLoader::loadFromHttp, jsonConverter)

        val gauge = Gauge<Int> { repo.getAll().size }
        metricRegistry.register(EXPERIMENTS_COUNT_METRIC, gauge)

        return repo
    }

    @Bean
    fun customConversions() = CustomConversions(mongoConverters)

    @Bean
    fun mongoExperimentsRepository(mongoTemplate: MongoTemplate, experimentsMongoMetricsReporter: ExperimentsMongoMetricsReporter): MongoExperimentsRepository {
        return MongoExperimentsRepository(mongoTemplate, experimentsMongoMetricsReporter)
    }

    @Bean
    fun experimentsRepository(
            fileBasedExperimentsRepository: FileBasedExperimentsRepository,
            mongoExperimentsRepository: CachedExperimentsRepository): ExperimentsRepository {
        return ExperimentsDoubleRepository(fileBasedExperimentsRepository, mongoExperimentsRepository)
    }

    @Bean
    fun measurementsRepository(druid: DruidClient, jsonConverter: JsonConverter,
                               @Value("\${druid.experimentsCube}") datasource: String): MeasurementsRepository
        = DruidMeasurementsRepository(druid, jsonConverter, datasource)

    @Bean
    fun fileRefresher(experimentsRepository: FileBasedExperimentsRepository): FileBasedExperimentsRepositoryRefresher {
        return FileBasedExperimentsRepositoryRefresher(experimentsRepository)
    }

    @Bean
    fun mongoRefresher(experimentsRepository: MongoExperimentsRepository): CachedExperimentsRepository {
        return CachedExperimentsRepository(experimentsRepository)
    }

    @Bean
    fun experimentsMetricsReporter(metricRegistry: MetricRegistry) : ExperimentsMongoMetricsReporter {
        return ExperimentsMongoMetricsReporter(metricRegistry)
    }

    @Bean
    fun crisisManagementFilter(crisisManagementProperties: CrisisManagementProperties) : CrisisManagementFilter  {
        if (crisisManagementProperties.enabled) {
            return WhitelistCrisisManagementFilter(crisisManagementProperties.whitelist)
        } else {
            return AllEnabledCrisisManagementFilter()
        }
    }

    @Bean
    @ConfigurationProperties("chi.crisisManagement")
    fun crisisManagementProperties(): CrisisManagementProperties {
        return CrisisManagementProperties()
    }
}

data class CrisisManagementProperties(var enabled: Boolean = false, var whitelist:List<String> = emptyList()) {
    init {
        enabled = false
        whitelist = ArrayList<String>()
    }
}
