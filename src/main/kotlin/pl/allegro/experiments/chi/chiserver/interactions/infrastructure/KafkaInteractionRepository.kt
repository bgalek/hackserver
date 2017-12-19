package pl.allegro.experiments.chi.chiserver.interactions.infrastructure

import org.springframework.kafka.core.KafkaOperations
import pl.allegro.experiments.chi.chiserver.interactions.Interaction
import pl.allegro.experiments.chi.chiserver.interactions.InteractionRepository
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter

class KafkaInteractionRepository(
        private val kafkaTemplate: KafkaOperations<String, ByteArray>,
        private val avroConverter: AvroConverter,
        private val kafkaTopic: String): InteractionRepository {

    override fun save(interaction: Interaction) {
        val data: ByteArray = serialize(interaction)
        kafkaTemplate.send(kafkaTopic, data)
    }

    private fun serialize(interaction: Interaction): ByteArray =
            avroConverter.toAvro(interaction)
                    .data()
}