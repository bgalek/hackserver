package pl.allegro.experiments.chi.chiserver.domain.experiments

import com.google.common.base.Preconditions
import com.google.common.base.Strings
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant
import java.time.ZonedDateTime

class Experiment(val id: String,
                 val variants: List<ExperimentVariant>,
                 val description: String?,
                 val owner: String?,
                 val reportingEnabled: Boolean,
                 val activeFrom: ZonedDateTime? = null,
                 val activeTo: ZonedDateTime? = null) {

    init {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "empty Experiment id")
        Preconditions.checkArgument(!variants.isEmpty(), "empty list of Variants")
    }

    fun isActive(now: ZonedDateTime): Boolean {
        return (activeFrom == null || now.isAfter(activeFrom)) && (activeTo == null || now.isBefore(activeTo))
    }
}
