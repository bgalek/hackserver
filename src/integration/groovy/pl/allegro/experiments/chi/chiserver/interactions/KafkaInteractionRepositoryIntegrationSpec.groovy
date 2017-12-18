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
import pl.allegro.experiments.chi.chiserver.domain.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.interactions.infrastructure.KafkaConfig
import pl.allegro.tech.common.andamio.server.cloud.CloudMetadata
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter
import spock.lang.Shared
import spock.lang.Unroll

import java.time.Instant
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

import static InteractionsIntegrationTestConfig.TEST_EXPERIMENT_ID
import static pl.allegro.experiments.chi.chiserver.interactions.InteractionsIntegrationTestConfig.TEST_EXPERIMENT_ID_WITH_DISABLED_REPORTING

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
    ExperimentsRepository experimentsRepository

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
        Interaction interaction = sampleInteraction(TEST_EXPERIMENT_ID)

        when:
        byte[] interactionAsBytes = avroConverter.toAvro(interaction).data()

        then:
        equalsIgnoringAppId( avroConverter.fromAvro(interactionAsBytes, 1, Interaction), interaction )
    }

    def "should save interaction"() {
        given:
        def interaction = sampleInteraction(TEST_EXPERIMENT_ID)

        when:
        def triedToSave = interactionRepository.save(interaction)
        def received = receiveInteraction()

        then:
        equalsIgnoringAppId(received, interaction)

        and:
        triedToSave
    }

    @Unroll
    def "should not save interaction when #error"() {
        when:
        def triedToSave = interactionRepository.save(interaction)

        then:
        !receivedAnyInteractions()

        and:
        !triedToSave

        where:
        interaction << [
                sampleInteraction('UNKNOWN EXPERIMENT ID'),
                sampleInteraction(TEST_EXPERIMENT_ID_WITH_DISABLED_REPORTING)
        ]
        error << [
                'there is no connected experiment',
                'experiment reporting is disabled'
        ]
    }

    def "should save interaction with null fields, when they are not required"() {
        given:
        def interaction = sampleInteractionWithNulls()

        when:
        def triedToSave = interactionRepository.save(interaction)

        then:
        receiveInteraction() == interaction

        and:
        triedToSave
    }

    def receivedAnyInteractions() {
        records.poll(1000, TimeUnit.MILLISECONDS) != null
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
        kafkaConfig.kafkaInteractionRepository(kafkaTemplate, avroConverter, TOPIC, experimentsRepository)
    }

    Interaction sampleInteraction(String experimentId) {
        new Interaction(
                "userId",
                "userCmId",
                experimentId,
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
                TEST_EXPERIMENT_ID,
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
