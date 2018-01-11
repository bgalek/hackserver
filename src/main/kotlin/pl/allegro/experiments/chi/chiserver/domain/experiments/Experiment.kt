package pl.allegro.experiments.chi.chiserver.domain.experiments

import java.time.ZonedDateTime

typealias ExperimentId = String

class Experiment(
    val id: ExperimentId,
    val variants: List<ExperimentVariant>,
    val description: String?,
    val owner: String?,
    val reportingEnabled: Boolean = true,
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Experiment

        if (id != other.id) return false
        if (variants != other.variants) return false
        if (description != other.description) return false
        if (owner != other.owner) return false
        if (reportingEnabled != other.reportingEnabled) return false
        if (activeFrom != other.activeFrom) return false
        if (activeTo != other.activeTo) return false
        if (measurements != other.measurements) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + variants.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (owner?.hashCode() ?: 0)
        result = 31 * result + reportingEnabled.hashCode()
        result = 31 * result + (activeFrom?.hashCode() ?: 0)
        result = 31 * result + (activeTo?.hashCode() ?: 0)
        result = 31 * result + (measurements?.hashCode() ?: 0)
        return result
    }
}

data class ExperimentMeasurements(val lastDayVisits: Int = 0) {}
