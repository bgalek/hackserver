package pl.allegro.experiments.chi.chiserver.interactions

import java.time.Instant

data class Interaction(
        val userId: String?,
        val userCmId: String?,
        val experimentId: String,
        val variantName: String,
        val internal: Boolean?,
        val deviceClass: String?,
        val interactionDate: Instant
)