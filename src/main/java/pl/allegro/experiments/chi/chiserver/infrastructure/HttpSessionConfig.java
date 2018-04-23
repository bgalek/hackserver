package pl.allegro.experiments.chi.chiserver.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.session.data.mongo.JdkMongoSessionConverter;
import org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession;

import java.time.Duration;

@EnableMongoHttpSession
class HttpSessionConfig {

    @Bean
    JdkMongoSessionConverter jdkMongoSessionConverter() {
        return new JdkMongoSessionConverter(Duration.ofDays(1));
    }
}
