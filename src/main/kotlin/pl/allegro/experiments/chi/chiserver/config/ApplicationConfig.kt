package pl.allegro.experiments.chi.chiserver.config

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.infrastructure.HttpContentLoader
import pl.allegro.experiments.chi.core.ExperimentsRepository
import pl.allegro.experiments.chi.persistence.FileBasedExperimentsRepository
import pl.allegro.experiments.chi.persistence.InMemoryExperimentsRepository
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
    fun httpContentLoader(restTemplate: RestTemplate) : HttpContentLoader {
        return HttpContentLoader(restTemplate)
    }

    @Bean
    fun localRepository() : InMemoryExperimentsRepository {
        return InMemoryExperimentsRepository(emptyList())
    }

    @Bean
    fun experimentsRepository(@Value("\${chi.experiments.file}") jsonUrl: String,
                              httpContentLoader: HttpContentLoader) : FileBasedExperimentsRepository {
        return FileBasedExperimentsRepository(jsonUrl, httpContentLoader::loadFromHttp)
    }
}
