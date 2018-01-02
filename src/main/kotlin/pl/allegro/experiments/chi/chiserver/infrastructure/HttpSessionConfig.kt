package pl.allegro.experiments.chi.chiserver.infrastructure

import org.springframework.context.annotation.Bean
import org.springframework.session.data.mongo.JdkMongoSessionConverter
import org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession

@EnableMongoHttpSession
class HttpSessionConfig {

    @Bean
    fun jdkMongoSessionConverter(): JdkMongoSessionConverter {
        return JdkMongoSessionConverter()
    }
}
