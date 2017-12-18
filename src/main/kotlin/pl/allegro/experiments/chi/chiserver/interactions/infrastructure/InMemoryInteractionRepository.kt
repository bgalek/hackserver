package pl.allegro.experiments.chi.chiserver.interactions.infrastructure

import pl.allegro.experiments.chi.chiserver.domain.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.interactions.Interaction
import pl.allegro.experiments.chi.chiserver.interactions.InteractionRepository
import kotlin.collections.ArrayList

class InMemoryInteractionRepository(private val experimentsRepository: ExperimentsRepository) : InteractionRepository {
    private val interactions: MutableList<Interaction> = ArrayList()

    override fun save(interaction: Interaction): Boolean {
        val reportingEnabled = experimentsRepository.reportingEnabled(interaction.experimentId)
        if (reportingEnabled) {
            interactions.add(interaction)
        }
        return reportingEnabled
    }

    fun interactionSaved(interaction: Interaction): Boolean {
        return interactions.contains(interaction)
    }
}