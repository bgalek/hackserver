package pl.allegro.experiments.chi.chiserver.interactions

import java.time.Instant

data class InteractionDto(
        val userId: String?,
        val userCmId: String?,
        val experimentId: String,
        val variantName: String,
        val internal: Boolean?,
        val deviceClass: String?,
        val interactionDate: Instant
) {
    fun toInteraction(): Interaction =
            Interaction(
                    userId,
                    userCmId,
                    experimentId,
                    variantName,
                    internal,
                    deviceClass,
                    interactionDate
            )
}