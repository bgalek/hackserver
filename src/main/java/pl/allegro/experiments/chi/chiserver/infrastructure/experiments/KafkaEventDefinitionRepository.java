package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import org.springframework.kafka.core.KafkaOperations;
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinitionRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.infrastructure.avro.AvroEventDefinition;
import pl.allegro.tech.common.andamio.avro.AvroConverter;

import java.util.List;

public class KafkaEventDefinitionRepository implements EventDefinitionRepository {
    private final KafkaOperations<String, byte[]> kafkaTemplate;
    private final AvroConverter avroConverter;
    private final String kafkaTopic;

    public KafkaEventDefinitionRepository(
            KafkaOperations<String, byte[]> kafkaTemplate,
            AvroConverter avroConverter,
            String kafkaTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.avroConverter = avroConverter;
        this.kafkaTopic = kafkaTopic;
    }

    @Override
    public void saveExperimentsEventDefinitions(List<ExperimentDefinition> experimentDefinitions) {
        List<AvroEventDefinition> toSave = AvroEventDefinition.listFrom(experimentDefinitions);
        toSave.forEach(it -> kafkaTemplate.send(kafkaTopic, serialize(it)));
    }

    private byte[] serialize(AvroEventDefinition eventDefinition) {
        return avroConverter.toAvro(eventDefinition).data();
    }
}
