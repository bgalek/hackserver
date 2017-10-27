package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import pl.allegro.experiments.chi.chiserver.assignments.ExperimentAssignmentRepository
import pl.allegro.experiments.chi.chiserver.assignments.ExperimentAssignment
import pl.allegro.experiments.chi.chiserver.logger
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter
import java.util.concurrent.CompletableFuture


class KafkaEventEmitter(
        private val kafkaTemplate: KafkaTemplate<String, ByteArray>,
        private val avroConverter: AvroConverter,
        private val kafkaTopic: String): ExperimentAssignmentRepository {

    companion object {
        private val logger by logger()
    }

    override fun save(experimentAssignment: ExperimentAssignment) {
        val data: ByteArray = serialize(experimentAssignment)
        val future = CompletableFuture<SendResult<String, ByteArray>>()
        val listenableFuture = kafkaTemplate.send(kafkaTopic, data)
        listenableFuture.addCallback(ToCompletableFutureCallback<SendResult<String, ByteArray>>(future))
        future.handle { result, exception -> exception }
                .thenApply { exception -> logIfFailed(exception) }
    }

    private fun logIfFailed(exception: Throwable?) {
        exception?.let {
            logger.error("Failed to send assignment event to kafka: ${exception.message}")
        }
    }

    private fun serialize(experimentAssignment: ExperimentAssignment): ByteArray =
            avroConverter.toAvro(experimentAssignment)
                    .data()

}