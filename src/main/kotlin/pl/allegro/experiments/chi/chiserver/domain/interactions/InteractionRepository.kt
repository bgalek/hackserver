package pl.allegro.experiments.chi.chiserver.domain.interactions

interface InteractionRepository {
    fun save(interaction: Interaction)
}