package pl.allegro.experiments.chi.chiserver.config

import com.codahale.metrics.Gauge
import com.codahale.metrics.MetricRegistry
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.api.v1.JsonConverter
import pl.allegro.experiments.chi.chiserver.infrastructure.ClientConnectionProperties
import pl.allegro.experiments.chi.chiserver.infrastructure.HttpContentLoader
import pl.allegro.experiments.chi.core.ExperimentsRepository
import pl.allegro.experiments.chi.persistence.FileBasedExperimentsRepository
import pl.allegro.tech.common.andamio.errors.jackson.ErrorsModule
import pl.allegro.tech.common.andamio.spring.client.RestTemplateFactory
import javax.annotation.PostConstruct

@Configuration
class ApplicationConfig {

    @Autowired
    lateinit var metricRegistry: MetricRegistry

    @Autowired
    lateinit var experimentsRepository: ExperimentsRepository

    @Bean
    fun restTemplate(
            factory: RestTemplateFactory,
            @Qualifier("chiExperimentsClient") clientConnectionProperties: ClientConnectionProperties): RestTemplate {
        val connectionConfig = clientConnectionProperties.toConfig()
        return factory.usingApacheHttp().create(connectionConfig)
    }

    @Bean
    fun errorsModule(): ErrorsModule = ErrorsModule.module()

    @Bean
    fun kotlinModule(): KotlinModule = KotlinModule()

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
        metricRegistry.register("experiments.count", gauge)
    }

    @Bean
    @ConfigurationProperties(prefix = "chi.experiments.connection")
    fun chiExperimentsClient(): ClientConnectionProperties = ClientConnectionProperties()
}
