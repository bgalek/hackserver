package pl.allegro.experiments.chi.chiserver.assignments

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.TopicPartition
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaOperations
import org.springframework.kafka.support.SendResult
import org.springframework.util.concurrent.ListenableFuture
import org.springframework.util.concurrent.SettableListenableFuture
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.assignments.infrastructure.CouldNotSendMessageToKafkaError
import pl.allegro.experiments.chi.chiserver.assignments.infrastructure.KafkaAssignmentRepository
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter

import java.time.Instant

class KafkaAssignmentRepositorySpec extends BaseIntegrationSpec {

    private static final String FAKE_TOPIC = "fakeTopic"

    @Autowired
    AvroConverter avroConverter

    def 'should send event to kafka when saving experiment assignment'() {
        given: "there is kafka repository"
        KafkaOperations fakeKafkaTemplate = Mock()
        KafkaAssignmentRepository kafkaExperimentAssignmentRepository = createKafkaExperimentAssignmentRepository(fakeKafkaTemplate)

        and:
        ListenableFuture expectedSendResult = new SettableListenableFuture<SendResult<String, byte[]>>()
        expectedSendResult.set(fakeSendResult(sampleExperimentAssignment()))

        when: 'we save assignment'
        kafkaExperimentAssignmentRepository.save(sampleExperimentAssignment())

        then: 'event is sent to kafka'
        1 * fakeKafkaTemplate.send(FAKE_TOPIC, _) >> expectedSendResult
    }

    def 'should send experiment assignment with empty values'() {
        given: "there is kafka repository"
        KafkaOperations fakeKafkaTemplate = Mock()
        KafkaAssignmentRepository kafkaExperimentAssignmentRepository = createKafkaExperimentAssignmentRepository(fakeKafkaTemplate)

        and:
        ListenableFuture expectedSendResult = new SettableListenableFuture<SendResult<String, byte[]>>()
        expectedSendResult.set(fakeSendResult(emptyExperimentAssignment()))

        when: 'we save assignment with empty values'
        kafkaExperimentAssignmentRepository.save(emptyExperimentAssignment())

        then: 'event is sent to kafka'
        1 * fakeKafkaTemplate.send(FAKE_TOPIC, _) >> expectedSendResult
    }

    def 'should throw error when something goes wrong'() {
        given: "there is kafka repository"
        KafkaOperations fakeKafkaTemplate = Mock()
        KafkaAssignmentRepository kafkaExperimentAssignmentRepository = createKafkaExperimentAssignmentRepository(fakeKafkaTemplate)

        and:
        ListenableFuture expectedSendResult = new SettableListenableFuture<SendResult<String, byte[]>>()
        expectedSendResult.cancel(true)
        fakeKafkaTemplate.send(FAKE_TOPIC, _) >> expectedSendResult

        when: 'something goes wrong'
        kafkaExperimentAssignmentRepository.save(sampleExperimentAssignment())

        then: 'we get error'
        thrown(CouldNotSendMessageToKafkaError)
    }

    def 'should throw error when sending reach timeout'() {
        given: "there is kafka repository"
        KafkaOperations fakeKafkaTemplate = Mock()
        KafkaAssignmentRepository kafkaExperimentAssignmentRepository = createKafkaExperimentAssignmentRepository(fakeKafkaTemplate)

        and:
        ListenableFuture expectedSendResult = new SettableListenableFuture<SendResult<String, byte[]>>()
        fakeKafkaTemplate.send(FAKE_TOPIC, _) >> expectedSendResult

        when: 'saving take to much time'
        kafkaExperimentAssignmentRepository.save(sampleExperimentAssignment())

        then: 'we get error'
        thrown(CouldNotSendMessageToKafkaError)
    }

    private KafkaAssignmentRepository createKafkaExperimentAssignmentRepository(KafkaOperations kafkaTemplate) {
        new KafkaAssignmentRepository(kafkaTemplate, avroConverter, FAKE_TOPIC, 100)
    }

    private SendResult<String, byte[]> fakeSendResult(Assignment experimentAssignmentAs) {
        new SendResult<String, byte[]>(
                new ProducerRecord<String, byte[]>(FAKE_TOPIC, avroConverter.toAvro(experimentAssignmentAs).data()),
                new RecordMetadata(new TopicPartition(FAKE_TOPIC, 10),
                        1, 1, 1, 1, 1, 1)
        )
    }

    private static Assignment sampleExperimentAssignment() {
        new Assignment(
                'userId',
                'userCmId',
                'experimentId',
                'variantName',
                true,
                true,
                "iphone",
                Instant.now()
        )
    }

    private static Assignment emptyExperimentAssignment() {
        new Assignment(
                null,
                null,
                'experimentId',
                'variantName',
                null,
                true,
                null,
                Instant.now()
        )
    }
}
