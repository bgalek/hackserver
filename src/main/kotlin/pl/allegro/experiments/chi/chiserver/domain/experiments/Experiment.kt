package pl.allegro.experiments.chi.chiserver.domain.experiments

import java.time.ZonedDateTime

typealias ExperimentId = String

class Experiment(
    val id: ExperimentId,
    val variants: List<ExperimentVariant>,
    val description: String?,
    val owner: String?,
    val reportingEnabled: Boolean,
    val activeFrom: ZonedDateTime? = null,
    val activeTo: ZonedDateTime? = null,
    val measurements: ExperimentMeasurements? = null
) {
    init {
        require(id.isNotBlank()) { "empty Experiment id" }
        require(!variants.isEmpty()) { "empty list of Variants" }
    }

    fun isActive(): Boolean {
        return isActiveAt(ZonedDateTime.now())
    }

    private fun isActiveAt(now: ZonedDateTime): Boolean {
        return (activeFrom == null || now.isAfter(activeFrom)) && (activeTo == null || now.isBefore(activeTo))
    }
}

data class ExperimentMeasurements(val lastDayVisits: Int = 0) {}