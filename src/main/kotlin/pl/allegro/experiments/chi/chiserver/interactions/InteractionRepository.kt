package pl.allegro.experiments.chi.chiserver.interactions

interface InteractionRepository {
    fun save(interaction: Interaction): Boolean
}