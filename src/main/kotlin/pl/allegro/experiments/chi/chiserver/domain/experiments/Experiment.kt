package pl.allegro.experiments.chi.chiserver.domain.experiments

import java.time.ZonedDateTime

typealias ExperimentId = String

data class Experiment(
    val id: ExperimentId,
    val variants: List<ExperimentVariant>,
    val description: String?,
    val documentLink: String?,
    val author: String?,
    val groups: List<String>,
    val reportingEnabled: Boolean = true,
    val activityPeriod: ActivityPeriod? = null,
    val measurements: ExperimentMeasurements? = null,
    //TODO move to UI layer
    val editable: Boolean? = null,
    val origin: String? = null
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

    fun isDraft(): Boolean {
        return status == ExperimentStatus.DRAFT
    }

    fun isEnded(): Boolean {
        return status == ExperimentStatus.ENDED
    }

    fun isActive(): Boolean {
        return status == ExperimentStatus.ACTIVE
    }

    fun isOverridable(): Boolean {
        return !isEnded()
    }

    fun start(experimentDurationDays: Long): Experiment {
        val newPeriod = ActivityPeriod(ZonedDateTime.now(), ZonedDateTime.now().plusDays(experimentDurationDays))
        return copy(activityPeriod = newPeriod)
    }

    fun prolong(experimentAdditionalDays: Long): Experiment {
        val newPeriod = ActivityPeriod(activityPeriod!!.activeFrom, activityPeriod.activeTo.plusDays(experimentAdditionalDays))
        return copy(activityPeriod = newPeriod)
    }

    fun withEditableFlag(editable: Boolean): Experiment {
        return copy(editable = editable)
    }

    fun withOrigin(origin: String): Experiment {
        return copy(origin = origin)
    }

    fun stop(): Experiment {
        return copy(activityPeriod = activityPeriod!!.endNow())
    }
}