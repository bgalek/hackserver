package pl.allegro.experiments.chi.chiserver.assignments

import java.time.Instant

data class ExperimentAssignmentDto(
        val userId: String?,
        val userCmId: String?,
        val experimentId: String,
        val variantName: String,
        val internal: Boolean?,
        val confirmed: Boolean,
        val deviceClass: String?,
        val assignmentDate: Instant
) {
    fun toEvent(): ExperimentAssignment =
            ExperimentAssignment(
                    userId,
                    userCmId,
                    experimentId,
                    variantName,
                    internal,
                    confirmed,
                    deviceClass,
                    assignmentDate
            )
}