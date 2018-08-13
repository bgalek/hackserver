package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.test.rule.KafkaEmbedded
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinitionSaver
import pl.allegro.experiments.chi.chiserver.infrastructure.avro.AvroEventDefinition
import pl.allegro.tech.common.andamio.avro.AvroConverter
import spock.lang.Ignore

import java.time.Instant
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit

@Ignore //TODO, naprawić, failuje losowo na Bamboo
@ContextConfiguration(classes = [FakeKafkaTestConfig])
class EventDefinitionSaverSpec extends BaseIntegrationSpec {

    @Autowired
    EventDefinitionSaver eventDefinitionSaver

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

    def "should convert event definition from avro and back again"() {
        given:
        def now = Instant.now()
        AvroEventDefinition eventDefinition = new AvroEventDefinition(
                'e', 'c', 'a', 'v', 'l', 'bn', now, now)

        when:
        byte[] toAvro = avroConverter.toAvro(eventDefinition).data()
        AvroEventDefinition fromAvro = avroConverter.fromAvro(toAvro, 1, AvroEventDefinition)

        then:
        eventDefinition.get__timestamp() == fromAvro.get__timestamp()
        eventDefinition.getSentAt() == fromAvro.getSentAt()
        eventDefinition.getCategory() == fromAvro.getCategory()
        eventDefinition.getLabel() == fromAvro.getLabel()
        eventDefinition.getValue() == fromAvro.getValue()
        eventDefinition.getAction() == fromAvro.getAction()
        eventDefinition.getBoxName() == fromAvro.getBoxName()
    }

    def "should save event definitions from existing experiments"() {
        given:
        String experimentId = UUID.randomUUID().toString()
        userProvider.user = new User('Anonymous', [], true)

        def request = [
                id              : experimentId,
                variantNames    : ['v2'],
                percentage      : 10,
                reportingType   : 'FRONTEND',
                eventDefinitions: [[
                        label: 'label1',
                        category: 'category1',
                        value   : 'value1',
                        action  : 'action1',
                        boxName : 'boxName1'
                ]],
                reportingEnabled: true
        ]
        and:
        restTemplate.postForEntity(localUrl('/api/admin/experiments'), request, Map)

        when:
        eventDefinitionSaver.saveCurrentEventDefinitions()
        AvroEventDefinition received = receiveEventDefinition()

        then:
        received.getCategory() == 'category1'
        received.getLabel() == 'label1'
        received.getValue() == 'value1'
        received.getAction() == 'action1'
        received.getBoxName() == 'boxName1'
    }

    def receiveEventDefinition() {
        ConsumerRecord record = records.poll(2000, TimeUnit.MILLISECONDS)
        byte[] recordAsBytes = record.value()
        avroConverter.fromAvro(
                recordAsBytes,
                1,
                AvroEventDefinition)
    }
}
