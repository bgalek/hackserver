package pl.allegro.tech.selfservice.newservicesingle.config

import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import pl.allegro.tech.common.andamio.errors.jackson.ErrorsModule
import pl.allegro.tech.common.andamio.spring.client.RestTemplateFactory
import pl.allegro.tech.selfservice.newservicesingle.domain.DomainConfiguration

@Configuration
@EnableConfigurationProperties(DomainConfiguration::class)
class ApplicationConfig {

    @Bean
    fun restTemplate(factory: RestTemplateFactory): RestTemplate = factory.usingApacheHttp().create()

    @Bean
    fun errorsModule(): ErrorsModule = ErrorsModule.module()

    @Bean
    fun kotlinModule() = KotlinModule()

    @Bean
    fun afterburnerModule() = AfterburnerModule()
}