package pl.allegro.experiments.chi.chiserver.interactions

import com.codahale.metrics.MetricRegistry
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.listener.MessageListener
import org.springframework.kafka.listener.config.ContainerProperties
import org.springframework.kafka.test.rule.KafkaEmbedded
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.interactions.infrastructure.KafkaConfig
import pl.allegro.tech.common.andamio.server.cloud.CloudMetadata
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter
import spock.lang.Shared

import java.time.Instant
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

@ContextConfiguration(classes = [InteractionsIntegrationTestConfig])
class KafkaInteractionRepositoryIntegrationSpec extends BaseIntegrationSpec {

    @Shared
    String TOPIC = "topic.t"

    @Shared
    String brokers

    KafkaMessageListenerContainer<String, byte[]> container

    BlockingQueue<ConsumerRecord<String, byte[]>> records

    @Shared
    KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, TOPIC)

    @Shared
    InteractionRepository interactionRepository

    @Autowired
    AvroConverter avroConverter

    @Autowired
    CloudMetadata cloudMetadata

    def setupSpec() {
        embeddedKafka.before()
        brokers = System.getProperties().getProperty("spring.embedded.kafka.brokers")
    }

    def setup() {
        Map consumerProperties = KafkaTestUtils.consumerProps("sender", "false", embeddedKafka)

        def consumerFactory = new DefaultKafkaConsumerFactory(consumerProperties)

        def containerProperties = new ContainerProperties(TOPIC)

        container = new KafkaMessageListenerContainer(consumerFactory, containerProperties)

        records = new LinkedBlockingQueue()

        container.setupMessageListener({record -> records.add(record)} as MessageListener)

        container.start()
        interactionRepository = kafkaInteractionRepository(brokers)
    }

    def cleanup() {
        container.stop()
    }

    def cleanupSpec() {
        embeddedKafka.after()
    }

    def "avro should work"() {
        given:
        Interaction interaction = sampleInteraction()

        when:
        byte[] interactionAsBytes = avroConverter.toAvro(interaction).data()

        then:
        equalsIgnoringAppId( avroConverter.fromAvro(interactionAsBytes, 1, Interaction), interaction )
    }

    def "should save interaction"() {
        given:
        def interaction = sampleInteraction()

        when:
        interactionRepository.save(interaction)
        def received = receiveInteraction()

        then:
        equalsIgnoringAppId(received, interaction)
    }

    def "should not save interaction with null fields"() {
        given:
        def interaction = sampleInteractionWithNulls()

        when:
        interactionRepository.save(interaction)

        then:
        receiveInteraction() == interaction
    }

    def receiveInteraction() {
        ConsumerRecord interactionRecord = records.poll(1000, TimeUnit.MILLISECONDS)
        byte[] interactionAsBytes = interactionRecord.value()
        avroConverter.fromAvro(
                interactionAsBytes,
                1,
                Interaction)
    }

    InteractionRepository kafkaInteractionRepository(String brokerString) {
        KafkaConfig kafkaConfig = new KafkaConfig()
        KafkaTemplate kafkaTemplate = kafkaConfig.kafkaTemplate(
                brokerString,
                brokerString,
                cloudMetadata,
                1,
                10,
                new MetricRegistry()
        )
        kafkaConfig.kafkaInteractionRepository(kafkaTemplate, avroConverter, TOPIC)
    }

    Interaction sampleInteraction() {
        new Interaction(
                "userId",
                "userCmId",
                "experimentId",
                "variantName",
                false,
                "iphone",
                Instant.EPOCH,
                "app-id"
        )
    }

    Interaction sampleInteractionWithNulls() {
        new Interaction(
                null,
                null,
                "experimentId",
                "variantName",
                null,
                null,
                Instant.EPOCH,
                null
        )
    }

    boolean equalsIgnoringAppId(Interaction a, Interaction b) {
        EqualsBuilder.reflectionEquals(a, b, ["appId"])
    }
}
