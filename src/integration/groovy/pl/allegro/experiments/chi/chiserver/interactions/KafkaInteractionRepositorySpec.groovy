package pl.allegro.experiments.chi.chiserver.interactions

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.TopicPartition
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaOperations
import org.springframework.kafka.support.SendResult
import org.springframework.util.concurrent.ListenableFuture
import org.springframework.util.concurrent.SettableListenableFuture
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.interactions.infrastructure.CouldNotSendMessageToKafkaError
import pl.allegro.experiments.chi.chiserver.interactions.infrastructure.KafkaInteractionRepository
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter

import java.time.Instant

class KafkaInteractionRepositorySpec extends BaseIntegrationSpec {

    String FAKE_TOPIC = "fakeTopic"

    @Autowired
    AvroConverter avroConverter

    def 'should send event to kafka when saving interaction'() {
        given:
        KafkaOperations fakeKafkaTemplate = Mock()
        KafkaInteractionRepository kafkaInteractionRepository = createKafkaInteractionRepository(fakeKafkaTemplate)

        and:
        ListenableFuture expectedSendResult = new SettableListenableFuture<SendResult<String, byte[]>>()
        expectedSendResult.set(fakeSendResult(sampleInteraction()))

        when:
        kafkaInteractionRepository.save(sampleInteraction())

        then:
        1 * fakeKafkaTemplate.send(FAKE_TOPIC, _) >> expectedSendResult
    }

    def 'should send interaction with empty values'() {
        given:
        KafkaOperations fakeKafkaTemplate = Mock()
        KafkaInteractionRepository kafkaInteractionRepository = createKafkaInteractionRepository(fakeKafkaTemplate)

        and:
        ListenableFuture expectedSendResult = new SettableListenableFuture<SendResult<String, byte[]>>()
        expectedSendResult.set(fakeSendResult(emptyInteraction()))

        when:
        kafkaInteractionRepository.save(emptyInteraction())

        then:
        1 * fakeKafkaTemplate.send(FAKE_TOPIC, _) >> expectedSendResult
    }

    def 'should throw error when something goes wrong'() {
        given:
        KafkaOperations fakeKafkaTemplate = Mock()
        KafkaInteractionRepository kafkaInteractionRepository = createKafkaInteractionRepository(fakeKafkaTemplate)

        and:
        ListenableFuture expectedSendResult = new SettableListenableFuture<SendResult<String, byte[]>>()
        expectedSendResult.cancel(true)
        fakeKafkaTemplate.send(FAKE_TOPIC, _) >> expectedSendResult

        when: 'something goes wrong'
        kafkaInteractionRepository.save(sampleInteraction())

        then:
        thrown(CouldNotSendMessageToKafkaError)
    }

    def 'should throw error when sending reach timeout'() {
        given:
        KafkaOperations fakeKafkaTemplate = Mock()
        KafkaInteractionRepository kafkaInteractionRepository = createKafkaInteractionRepository(fakeKafkaTemplate)

        and:
        ListenableFuture expectedSendResult = new SettableListenableFuture<SendResult<String, byte[]>>()
        fakeKafkaTemplate.send(FAKE_TOPIC, _) >> expectedSendResult

        when: 'saving take to much time'
        kafkaInteractionRepository.save(sampleInteraction())

        then:
        thrown(CouldNotSendMessageToKafkaError)
    }

    KafkaInteractionRepository createKafkaInteractionRepository(KafkaOperations kafkaTemplate) {
        new KafkaInteractionRepository(kafkaTemplate, avroConverter, FAKE_TOPIC, 100)
    }

    SendResult<String, byte[]> fakeSendResult(Interaction interaction) {
        new SendResult<String, byte[]>(
                new ProducerRecord<String, byte[]>(FAKE_TOPIC, avroConverter.toAvro(interaction).data()),
                new RecordMetadata(new TopicPartition(FAKE_TOPIC, 10),
                        1, 1, 1, 1, 1, 1)
        )
    }

    Interaction sampleInteraction() {
        new Interaction(
                'userId',
                'userCmId',
                'experimentId',
                'variantName',
                true,
                "iphone",
                Instant.now()
        )
    }

    private static Interaction emptyInteraction() {
        new Interaction(
                null,
                null,
                'experimentId',
                'variantName',
                null,
                null,
                Instant.now()
        )
    }
}
