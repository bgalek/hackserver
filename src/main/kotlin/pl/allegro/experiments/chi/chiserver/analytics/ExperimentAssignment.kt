package pl.allegro.experiments.chi.chiserver.analytics

import java.time.Instant

data class ExperimentAssignment(
        val userId: String?,
        val userCmId: String?,
        val experimentId: String,
        val variantName: String,
        val internal: Boolean?,
        val confirmed: Boolean?,
        val deviceClass: String?,
        val assignmentDate: Instant
)