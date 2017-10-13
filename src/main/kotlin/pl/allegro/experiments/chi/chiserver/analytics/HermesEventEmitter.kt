package pl.allegro.experiments.chi.chiserver.analytics

import pl.allegro.tech.common.andamio.spring.avro.AvroConverter
import pl.allegro.tech.hermes.client.HermesClient
import java.util.concurrent.CompletableFuture

class HermesEventEmitter(private val hermesClient: HermesClient,
                         private val avroConverter: AvroConverter,
                         private val hermesTopicProperties: HermesTopicProperties) {

    //TODO: return something better than CompletableFuture<Boolean>
    fun emit(experimentAssignment: ExperimentAssignment): CompletableFuture<Boolean> {
        val serializedData = serialize(experimentAssignment)
        return hermesClient.publishAvro(hermesTopicProperties.topic, hermesTopicProperties.schemaVersion, serializedData)
                .thenApply { hermesResponse -> hermesResponse.isSuccess }
    }

    private fun  serialize(experimentAssignment: ExperimentAssignment) =
        avroConverter.toAvro(experimentAssignment)
                .data()
}