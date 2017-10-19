package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import pl.allegro.experiments.chi.chiserver.assignments.EventEmitter
import pl.allegro.experiments.chi.chiserver.assignments.ExperimentAssignmentEvent
import pl.allegro.tech.common.andamio.spring.avro.AvroConverter
import pl.allegro.tech.hermes.client.HermesClient
import java.util.concurrent.CompletableFuture

class HermesEventEmitter(private val hermesClient: HermesClient,
                         private val avroConverter: AvroConverter,
                         private val hermesTopicProperties: HermesTopicProperties): EventEmitter {

    //TODO: return something better than CompletableFuture<Boolean>
    override fun emit(experimentAssignment: ExperimentAssignmentEvent): CompletableFuture<Boolean> {
        val serializedData = serialize(experimentAssignment)
        return hermesClient.publishAvro(hermesTopicProperties.topic, hermesTopicProperties.schemaVersion, serializedData)
                .thenApply { hermesResponse -> hermesResponse.isSuccess }
    }

    private fun serialize(experimentAssignment: ExperimentAssignmentEvent) =
        avroConverter.toAvro(experimentAssignment)
                .data()
}