package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import pl.allegro.experiments.chi.chiserver.assignments.EventEmitter
import pl.allegro.experiments.chi.chiserver.assignments.ExperimentAssignmentEvent
import pl.allegro.experiments.chi.chiserver.logger
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter
import java.util.concurrent.CompletableFuture


//TODO: consult Lukasz Szulc, how to send messages to kafka.
class KafkaEventEmitter(
        private val kafkaTemplate: KafkaTemplate<String, ByteArray>,
        private val avroConverter: AvroConverter,
        private val kafkaTopic: String): EventEmitter {

    companion object {
        private val logger by logger()
    }

    override fun emit(experimentAssignment: ExperimentAssignmentEvent): CompletableFuture<Unit> {
        val data: ByteArray = serialize(experimentAssignment)
        val future = CompletableFuture<SendResult<String, ByteArray>>()
        val listenableFuture = kafkaTemplate.send(kafkaTopic, data)
        listenableFuture.addCallback(ToCompletableFutureCallback<SendResult<String, ByteArray>>(future))
        return future.handle { result, exception -> exception }
                .thenApply { exception -> logIfFailed(exception) }
    }

    private fun logIfFailed(exception: Throwable?): Unit {
        exception?.let {
            logger.error("Failed to send assignment event to kafka: ${exception.message}")
        }
    }

    private fun serialize(experimentAssignment: ExperimentAssignmentEvent): ByteArray =
            avroConverter.toAvro(experimentAssignment)
                    .data()

}