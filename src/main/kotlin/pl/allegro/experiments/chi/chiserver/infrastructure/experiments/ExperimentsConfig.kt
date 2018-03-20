package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import com.codahale.metrics.Gauge
import com.codahale.metrics.MetricRegistry
import com.google.gson.Gson
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
import pl.allegro.experiments.chi.chiserver.infrastructure.druid.DruidClient
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters.DateTimeDeserializer
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters.DateTimeSerializer
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters.ExperimentDeserializer
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters.ExperimentSerializer

@Configuration
class ExperimentsConfig {
    private val EXPERIMENTS_COUNT_ALL_METRIC =  "experiments.count.all";
    private val EXPERIMENTS_COUNT_LIVE_METRIC = "experiments.count.live";

    @Bean
    fun fileBasedExperimentsRepository(
        @Value("\${chi.experiments.file}") jsonUrl: String,
        restTemplate: RestTemplate, jsonConverter: Gson
    ): FileBasedExperimentsRepository {
        val httpContentLoader = HttpContentLoader(restTemplate)
        return FileBasedExperimentsRepository(jsonUrl, httpContentLoader, jsonConverter, null)
    }

    @Bean
    fun customConversions(
            dateTimeSerializer: DateTimeSerializer,
            dateTimeDeserializer: DateTimeDeserializer,
            experimentSerializer: ExperimentSerializer,
            experimentDeserializer: ExperimentDeserializer): CustomConversions {
        return CustomConversions(listOf(
                dateTimeDeserializer,
                dateTimeSerializer,
                experimentSerializer,
                experimentDeserializer
        ))
    }

    @Bean
    fun mongoExperimentsRepository(mongoTemplate: MongoTemplate, experimentsMongoMetricsReporter: ExperimentsMongoMetricsReporter): MongoExperimentsRepository {
        return MongoExperimentsRepository(mongoTemplate, experimentsMongoMetricsReporter)
    }

    @Bean
    fun experimentsRepository(
            fileBasedExperimentsRepository: FileBasedExperimentsRepository,
            mongoExperimentsRepository: CachedExperimentsRepository,
            metricRegistry: MetricRegistry): ExperimentsRepository {

        val repo = ExperimentsDoubleRepository(fileBasedExperimentsRepository, mongoExperimentsRepository)

        val gaugeAll = Gauge<Int> { repo.getAll().size }
        metricRegistry.register(EXPERIMENTS_COUNT_ALL_METRIC, gaugeAll)

        val gaugeLive = Gauge<Int> { repo.assignable().size }
        metricRegistry.register(EXPERIMENTS_COUNT_LIVE_METRIC, gaugeLive)

        return repo
    }

    @Bean
    fun measurementsRepository(druid: DruidClient, jsonConverter: Gson,
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
