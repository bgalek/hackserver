package pl.allegro.experiments.chi.chiserver.interactions

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.test.rule.KafkaEmbedded
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.interactions.Interaction
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.FakeKafkaTestConfig
import pl.allegro.experiments.chi.chiserver.infrastructure.interactions.KafkaInteractionRepository
import pl.allegro.tech.common.andamio.avro.AvroConverter
import spock.lang.Ignore

import java.time.Instant
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit

import static pl.allegro.experiments.chi.chiserver.utils.SampleInMemoryExperimentsRepository.TEST_EXPERIMENT_ID

@Ignore //TODO, naprawić, failuje losowo na Bamboo
@ContextConfiguration(classes = [FakeKafkaTestConfig])
class KafkaInteractionRepositoryIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    KafkaInteractionRepository interactionRepository

    @Autowired
    ExperimentsRepository experimentsRepository

    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    UserProvider userProvider

    @Autowired
    AvroConverter avroConverter

    @Autowired
    KafkaEmbedded embeddedKafka

    @Autowired
    KafkaMessageListenerContainer<String, byte[]> container

    @Autowired
    BlockingQueue<ConsumerRecord<String, byte[]>> records

    def setup() {
        container.start()
    }

    def cleanup() {
        container.stop()
    }

    def "should convert bytes to Interaction and back again"() {
        given:
        Interaction interaction = sampleInteraction()

        when:
        byte[] interactionAsBytes = avroConverter.toAvro(interaction).data()

        then:
        equalsIgnoringAppId(avroConverter.fromAvro(interactionAsBytes, 1, Interaction), interaction)
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

    def "should save interaction with null fields, when they are not required"() {
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

    Interaction sampleInteraction() {
        new Interaction(
                "userId",
                "userCmId",
                TEST_EXPERIMENT_ID,
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