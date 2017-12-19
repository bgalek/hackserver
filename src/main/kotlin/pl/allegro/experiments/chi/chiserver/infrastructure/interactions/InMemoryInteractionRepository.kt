package pl.allegro.experiments.chi.chiserver.infrastructure.interactions

import pl.allegro.experiments.chi.chiserver.domain.interactions.Interaction
import pl.allegro.experiments.chi.chiserver.domain.interactions.InteractionRepository
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