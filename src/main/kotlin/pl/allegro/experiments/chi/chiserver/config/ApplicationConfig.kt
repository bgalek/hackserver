package pl.allegro.experiments.chi.chiserver.config

import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.domain.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.FileBasedExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.InMemoryExperimentsRepository
import pl.allegro.tech.common.andamio.errors.jackson.ErrorsModule
import pl.allegro.tech.common.andamio.spring.client.RestTemplateFactory

@Configuration
class ApplicationConfig {

    @Bean
    fun restTemplate(factory: RestTemplateFactory): RestTemplate = factory.usingApacheHttp().create()

    @Bean
    fun errorsModule(): ErrorsModule = ErrorsModule.module()

    @Bean
    fun kotlinModule() = KotlinModule()

    @Bean
    fun afterburnerModule() = AfterburnerModule()

    @Bean
    fun fileBasedExperimentsRepository(restTemplate: RestTemplate, @Value("\${chi.experiments.file}") jsonUrl: String): FileBasedExperimentsRepository {
        return FileBasedExperimentsRepository(jsonUrl, restTemplate)
    }

    @Bean
    fun inMemoryExperimentsRepository(fileBasedExperimentsRepository: FileBasedExperimentsRepository) : InMemoryExperimentsRepository {
        return InMemoryExperimentsRepository(emptyList())
    }

    @Bean
    fun experimentsRepository(inMemoryExperimentsRepository: InMemoryExperimentsRepository) : ExperimentsRepository {
        return inMemoryExperimentsRepository
    }
}
