package pl.allegro.experiments.chi.chiserver.domain.experiments

import java.time.ZonedDateTime

typealias ExperimentId = String

enum class ExperimentStatus {
    DRAFT, PLANNED, ACTIVE, ENDED
}

class Experiment(
    val id: ExperimentId,
    val variants: List<ExperimentVariant>,
    val description: String?,
    val author: String?,
    val groups: List<String>,
    val reportingEnabled: Boolean = true,
    val activityPeriod: ActivityPeriod?,
    val measurements: ExperimentMeasurements? = null
) {

    val status = when {
        activityPeriod == null -> ExperimentStatus.DRAFT
        activityPeriod.activeTo < ZonedDateTime.now() -> ExperimentStatus.ENDED
        activityPeriod.activeFrom > ZonedDateTime.now() -> ExperimentStatus.PLANNED
        else -> ExperimentStatus.ACTIVE
    }

    init {
        require(id.isNotBlank()) { "empty Experiment id" }
        require(!variants.isEmpty()) { "empty list of Variants" }
    }

    fun isActive(): Boolean {
        return status == ExperimentStatus.ACTIVE
    }

    fun isEnded(): Boolean {
        return status == ExperimentStatus.ENDED
    }

    fun isAssignable(): Boolean {
        return isActive()
    }

    fun isOverridable(): Boolean {
        return !isEnded()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Experiment

        if (id != other.id) return false
        if (variants != other.variants) return false
        if (description != other.description) return false
        if (author != other.author) return false
        if (groups != other.groups) return false
        if (reportingEnabled != other.reportingEnabled) return false
        if (activityPeriod != other.activityPeriod) return false
        if (measurements != other.measurements) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + variants.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (author?.hashCode() ?: 0)
        result = 31 * result + groups.hashCode()
        result = 31 * result + reportingEnabled.hashCode()
        result = 31 * result + (activityPeriod?.hashCode() ?: 0)
        result = 31 * result + (measurements?.hashCode() ?: 0)
        return result
    }


}

data class ExperimentMeasurements(val lastDayVisits: Int = 0) {}

data class ActivityPeriod (val activeFrom: ZonedDateTime, val activeTo: ZonedDateTime)
