package pl.allegro.experiments.chi.chiserver.infrastructure.interactions

import org.springframework.kafka.core.KafkaOperations
import org.springframework.kafka.support.SendResult
import pl.allegro.experiments.chi.chiserver.domain.interactions.InteractionRepository
import pl.allegro.experiments.chi.chiserver.domain.interactions.Interaction
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

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
