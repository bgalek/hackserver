package pl.allegro.experiments.chi.chiserver.infrastructure.interactions;

import org.springframework.kafka.core.KafkaOperations;
import pl.allegro.experiments.chi.chiserver.domain.interactions.Interaction;
import pl.allegro.experiments.chi.chiserver.domain.interactions.InteractionRepository;
import pl.allegro.tech.common.andamio.avro.AvroConverter;

public class KafkaInteractionRepository implements InteractionRepository {
    private final KafkaOperations<String, byte[]> kafkaTemplate;
    private final AvroConverter avroConverter;
    private final String kafkaTopic;

    public KafkaInteractionRepository(
            KafkaOperations<String, byte[]> kafkaTemplate,
            AvroConverter avroConverter,
            String kafkaTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.avroConverter = avroConverter;
        this.kafkaTopic = kafkaTopic;
    }

    @Override
    public void save(Interaction interaction) {
        byte[] data = serialize(interaction);
        kafkaTemplate.send(kafkaTopic, data);
    }

    private byte[] serialize(Interaction interaction) {
        return avroConverter.toAvro(interaction).data();
    }
}
