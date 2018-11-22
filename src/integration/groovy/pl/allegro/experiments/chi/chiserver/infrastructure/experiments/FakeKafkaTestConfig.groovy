package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.listener.MessageListener
import org.springframework.kafka.test.rule.KafkaEmbedded
import org.springframework.kafka.test.utils.KafkaTestUtils
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinitionRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.interactions.KafkaConfig
import pl.allegro.experiments.chi.chiserver.infrastructure.interactions.KafkaInteractionRepository
import pl.allegro.tech.common.andamio.avro.AvroConverter
import pl.allegro.tech.common.andamio.server.cloud.CloudMetadata

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

//@Configuration
@Deprecated
class FakeKafkaTestConfig {

    @Autowired
    CloudMetadata cloudMetadata

    @Autowired
    AvroConverter avroConverter

    static String TOPIC = "topic.t"

    @Bean
    KafkaEmbedded kafkaEmbedded() {
        new KafkaEmbedded(1, true, TOPIC)
    }

    @Bean
    KafkaMessageListenerContainer kafkaMessageListenerContainer(
            KafkaEmbedded embeddedKafka,
            BlockingQueue<ConsumerRecord<String, byte[]>> records) {

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                "sampleRawConsumer", "false", embeddedKafka)
        consumerProps.put("auto.offset.reset", "earliest")

        def consumerFactory = new DefaultKafkaConsumerFactory(consumerProps)

        def containerProperties = new ContainerProperties(TOPIC)

        def container = new KafkaMessageListenerContainer(consumerFactory, containerProperties)
        container.setupMessageListener({record -> records.add(record)} as MessageListener)
        container
    }

    @Primary
    @Bean
    EventDefinitionRepository eventDefinitionRepository(KafkaEmbedded kafkaEmbedded) {

        String brokers = System.getProperties().getProperty("spring.embedded.kafka.brokers")

        KafkaConfig kafkaConfig = new KafkaConfig()
        KafkaTemplate kafkaTemplate = kafkaConfig.kafkaTemplate(
                brokers,
                brokers,
                cloudMetadata,
                1,
                10,
                new SimpleMeterRegistry()
        )
        kafkaConfig.kafkaEventDefinitionRepository(kafkaTemplate, avroConverter, TOPIC)
    }

    @Bean
    KafkaInteractionRepository kafkaInteractionRepository(KafkaEmbedded kafkaEmbedded) {
        String brokers = System.getProperties().getProperty("spring.embedded.kafka.brokers")

        KafkaConfig kafkaConfig = new KafkaConfig()
        KafkaTemplate kafkaTemplate = kafkaConfig.kafkaTemplate(
                brokers,
                brokers,
                cloudMetadata,
                1,
                10,
                new SimpleMeterRegistry()
        )
        (KafkaInteractionRepository) kafkaConfig.kafkaInteractionRepository(kafkaTemplate, avroConverter, TOPIC)
    }

    @Bean
    BlockingQueue<ConsumerRecord<String, byte[]>> records() {
        new LinkedBlockingQueue()
    }
}
