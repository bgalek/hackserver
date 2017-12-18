package pl.allegro.experiments.chi.chiserver.interactions.infrastructure

import org.springframework.kafka.core.KafkaOperations
import org.springframework.kafka.support.SendResult
import pl.allegro.experiments.chi.chiserver.domain.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.interactions.InteractionRepository
import pl.allegro.experiments.chi.chiserver.interactions.Interaction
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class KafkaInteractionRepository(
        private val kafkaTemplate: KafkaOperations<String, ByteArray>,
        private val avroConverter: AvroConverter,
        private val kafkaTopic: String,
        private val experimentsRepository: ExperimentsRepository): InteractionRepository {

    override fun save(interaction: Interaction): Boolean {
        val reportingEnabled = experimentsRepository.reportingEnabled(interaction.experimentId)
        if (reportingEnabled) {
            val data: ByteArray = serialize(interaction)
            kafkaTemplate.send(kafkaTopic, data)
        }
        return reportingEnabled
    }

    private fun serialize(interaction: Interaction): ByteArray =
            avroConverter.toAvro(interaction)
                    .data()
}
