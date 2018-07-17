package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaOperations;
import pl.allegro.experiments.chi.chiserver.application.experiments.ExperimentsController;
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinitionRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.infrastructure.avro.AvroEventDefinition;
import pl.allegro.tech.common.andamio.avro.AvroConverter;

import java.util.List;

public class KafkaEventDefinitionRepository implements EventDefinitionRepository {
    private static final Logger logger = LoggerFactory.getLogger(KafkaEventDefinitionRepository.class);
    private final KafkaOperations<String, byte[]> kafkaTemplate;
    private final AvroConverter avroConverter;
    private final String kafkaTopic;

    public KafkaEventDefinitionRepository(
            KafkaOperations<String, byte[]> kafkaTemplate,
            AvroConverter avroConverter,
            String kafkaTopic) {
        Preconditions.checkNotNull(kafkaTemplate);
        Preconditions.checkNotNull(avroConverter);
        Preconditions.checkNotNull(kafkaTopic);
        this.kafkaTemplate = kafkaTemplate;
        this.avroConverter = avroConverter;
        this.kafkaTopic = kafkaTopic;
    }

    @Override
    public void saveExperimentsEventDefinitions(List<ExperimentDefinition> experimentDefinitions) {
        experimentDefinitions.forEach(experimentDefinition ->
                logger.info("Saving event definitions of experiment " + experimentDefinition.getId() + ": \n" + experimentDefinition.getReportingDefinition().toString() + "\n"));
        List<AvroEventDefinition> toSave = AvroEventDefinition.listFrom(experimentDefinitions);
        toSave.forEach(it -> kafkaTemplate.send(kafkaTopic, serialize(it)));
    }

    private byte[] serialize(AvroEventDefinition eventDefinition) {
        return avroConverter.toAvro(eventDefinition).data();
    }
}
