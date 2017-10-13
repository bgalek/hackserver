package pl.allegro.experiments.chi.chiserver

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.allegro.experiments.chi.chiserver.analytics.HermesEventEmitter
import pl.allegro.experiments.chi.chiserver.analytics.HermesTopicProperties
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter
import pl.allegro.tech.hermes.client.HermesClient

@Configuration
class HermesTestConfig {

    @Bean("hermesEventEmitter")
    HermesEventEmitter hermesEventEmitter(
            HermesClient hermesClient,
            AvroConverter avroConverter,
            HermesTopicProperties hermesTopicProperties) {
        return new HermesEventEmitter(hermesClient, avroConverter, hermesTopicProperties)
    }
}
