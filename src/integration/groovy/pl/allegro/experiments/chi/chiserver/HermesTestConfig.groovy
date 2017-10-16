package pl.allegro.experiments.chi.chiserver

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.allegro.experiments.chi.chiserver.analytics.InMemoryEventEmitter
import pl.allegro.experiments.chi.chiserver.analytics.infrastructure.HermesEventEmitter
import pl.allegro.experiments.chi.chiserver.analytics.infrastructure.HermesTopicProperties
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter
import pl.allegro.tech.hermes.client.HermesClient

@Configuration
class HermesTestConfig {

    @Bean("eventEmitter")
    InMemoryEventEmitter inMemoryEventEmitter() {
        return new InMemoryEventEmitter()
    }

    @Bean("hermesEventEmitter")
    HermesEventEmitter hermesEventEmitter(
            HermesClient hermesClient,
            AvroConverter avroConverter,
            HermesTopicProperties hermesTopicProperties) {
        return new HermesEventEmitter(hermesClient, avroConverter, hermesTopicProperties)
    }
}
