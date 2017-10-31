package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import org.springframework.kafka.core.KafkaOperations
import org.springframework.kafka.support.SendResult
import pl.allegro.experiments.chi.chiserver.assignments.ExperimentAssignmentRepository
import pl.allegro.experiments.chi.chiserver.assignments.ExperimentAssignment
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class KafkaAssignmentRepository(
        private val kafkaTemplate: KafkaOperations<String, ByteArray>,
        private val avroConverter: AvroConverter,
        private val kafkaTopic: String,
        private val sendTimeout: Long): ExperimentAssignmentRepository {

    override fun save(experimentAssignment: ExperimentAssignment) {
        val data: ByteArray = serialize(experimentAssignment)
        val future = CompletableFuture<SendResult<String, ByteArray>>()
        val listenableFuture = kafkaTemplate.send(kafkaTopic, data)
        listenableFuture.addCallback(ToCompletableFutureCallback<SendResult<String, ByteArray>>(future))
        try {
            future.exceptionally {
                throw it
            }.get(sendTimeout, TimeUnit.MILLISECONDS)
        } catch (e: Exception) {
            throw CouldNotSendMessageToKafkaError("Could not send assignment to kafka, $e")
        }
    }

    private fun serialize(experimentAssignment: ExperimentAssignment): ByteArray =
            avroConverter.toAvro(experimentAssignment)
                    .data()
}