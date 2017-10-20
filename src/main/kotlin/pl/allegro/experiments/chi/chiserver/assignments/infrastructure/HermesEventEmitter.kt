package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import pl.allegro.experiments.chi.chiserver.assignments.EventEmitter
import pl.allegro.experiments.chi.chiserver.assignments.ExperimentAssignmentEvent
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter
import pl.allegro.tech.hermes.client.HermesClient
import java.util.concurrent.CompletableFuture

//TODO: remove hermes event emitter we would use kafka
class HermesEventEmitter(private val hermesClient: HermesClient,
                         private val avroConverter: AvroConverter,
                         private val hermesTopicProperties: HermesTopicProperties): EventEmitter {

    //TODO: return something better than CompletableFuture<Boolean>
    override fun emit(experimentAssignment: ExperimentAssignmentEvent): CompletableFuture<Unit> {
        val serializedData = serialize(experimentAssignment)
        return hermesClient.publishAvro(hermesTopicProperties.topic, hermesTopicProperties.schemaVersion, serializedData)
                .thenApply { hermesResponse -> null }
    }

    private fun serialize(experimentAssignment: ExperimentAssignmentEvent): ByteArray =
        avroConverter.toAvro(experimentAssignment)
                .data()
}