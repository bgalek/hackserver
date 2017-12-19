package pl.allegro.experiments.chi.chiserver.interactions

import pl.allegro.experiments.chi.chiserver.domain.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.interactions.v1.InvalidFormatException

class InteractionsFactory(
        private val interactionsMetricsReporter : InteractionsMetricsReporter,
        private val experimentsRepository: ExperimentsRepository,
        private val interactionConverter: InteractionConverter) {

    fun fromJson(json: String): List<Interaction> {
        try {
            val interactions = interactionConverter.fromJson(json)
            val filtered = interactions.filter { interaction ->
                val experiment = experimentsRepository.getExperiment(interaction.experimentId)
                experiment != null && experiment.reportingEnabled
            }

            interactionsMetricsReporter.meterIgnored(interactions.size - filtered.size)

            return filtered
        } catch (e: Exception) {
            throw InvalidFormatException("Cant deserialize Interaction. Invalid format.")
        }
    }
}