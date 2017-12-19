package pl.allegro.experiments.chi.chiserver.domain.interactions

import pl.allegro.experiments.chi.chiserver.application.interactions.v1.InvalidFormatException
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository


class InteractionsFactory(
        private val experimentsRepository: ExperimentsRepository,
        private val interactionConverter: InteractionConverter) {

    fun fromJson(json: String): List<Interaction> {
        try {
            val interactions = interactionConverter.fromJson(json)
            return interactions.filter { interaction ->
                val experiment = experimentsRepository.getExperiment(interaction.experimentId)
                experiment != null && experiment.reportingEnabled
            }
        } catch (e: Exception) {
            throw InvalidFormatException("Cant deserialize Interaction. Invalid format.")
        }
    }
}