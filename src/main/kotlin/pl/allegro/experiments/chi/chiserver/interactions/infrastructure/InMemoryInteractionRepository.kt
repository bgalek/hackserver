package pl.allegro.experiments.chi.chiserver.interactions.infrastructure

import pl.allegro.experiments.chi.chiserver.interactions.Interaction
import pl.allegro.experiments.chi.chiserver.interactions.InteractionRepository
import java.util.*

class InMemoryInteractionRepository : InteractionRepository {
    private val interactions: MutableList<Interaction> = LinkedList()

    override fun save(interaction: Interaction) {
        interactions.add(interaction)
    }

    fun assertInteractionSaved(interaction: Interaction): Boolean {
        return interactions.contains(interaction)
    }
}