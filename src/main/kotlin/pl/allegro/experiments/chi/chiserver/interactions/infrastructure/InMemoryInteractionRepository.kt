package pl.allegro.experiments.chi.chiserver.interactions.infrastructure

import pl.allegro.experiments.chi.chiserver.domain.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.interactions.Interaction
import pl.allegro.experiments.chi.chiserver.interactions.InteractionRepository
import kotlin.collections.ArrayList

class InMemoryInteractionRepository : InteractionRepository {
    private val interactions: MutableList<Interaction> = ArrayList()

    override fun save(interaction: Interaction) {
        interactions.add(interaction)
    }

    fun interactionSaved(interaction: Interaction): Boolean {
        return interactions.contains(interaction)
    }
}