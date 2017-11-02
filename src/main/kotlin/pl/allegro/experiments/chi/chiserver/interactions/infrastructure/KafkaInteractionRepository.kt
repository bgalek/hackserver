package pl.allegro.experiments.chi.chiserver.interactions.infrastructure

import org.springframework.kafka.core.KafkaOperations
import org.springframework.kafka.support.SendResult
import pl.allegro.experiments.chi.chiserver.interactions.InteractionRepository
import pl.allegro.experiments.chi.chiserver.interactions.Interaction
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class KafkaInteractionRepository(
        private val kafkaTemplate: KafkaOperations<String, ByteArray>,
        private val avroConverter: AvroConverter,
        private val kafkaTopic: String,
        private val sendTimeout: Long): InteractionRepository {

    override fun save(interaction: Interaction) {
        val data: ByteArray = serialize(interaction)
        val future = CompletableFuture<SendResult<String, ByteArray>>()
        val listenableFuture = kafkaTemplate.send(kafkaTopic, data)
        listenableFuture.addCallback(ToCompletableFutureCallback<SendResult<String, ByteArray>>(future))
        try {
            future.exceptionally {
                throw it
            }.get(sendTimeout, TimeUnit.MILLISECONDS)
        } catch (e: Exception) {
            throw CouldNotSendMessageToKafkaError("Could not send interaction to kafka, $e")
        }
    }

    private fun serialize(interaction: Interaction): ByteArray =
            avroConverter.toAvro(interaction)
                    .data()
}