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

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit

@ContextConfiguration(classes = [EventDefinitionSaverTestConfig])
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
        embeddedKafka.after()
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
        ZonedDateTime now = Clock.systemUTC().instant().atZone(ZoneId.of("UTC"))

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
        received.getSentAt().isAfter(now.toInstant())
    }

    def receiveEventDefinition() {
        ConsumerRecord record = records.poll(1000, TimeUnit.MILLISECONDS)
        byte[] recordAsBytes = record.value()
        avroConverter.fromAvro(
                recordAsBytes,
                1,
                AvroEventDefinition)
    }
}
