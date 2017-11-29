package pl.allegro.experiments.chi.chiserver.domain

import com.google.common.base.Preconditions
import com.google.common.base.Strings
import com.google.common.collect.ImmutableList
import java.time.ZonedDateTime

class Experiment @JvmOverloads internal constructor(val id: String, variants: List<ExperimentVariant>, val activeFrom: ZonedDateTime? = null, val activeTo: ZonedDateTime? = null) {
    val variants: List<ExperimentVariant>

    init {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "empty Experiment id")
        Preconditions.checkArgument(!variants.isEmpty(), "empty list of Variants")
        this.variants = ImmutableList.copyOf(variants)
    }

    fun isActive(now: ZonedDateTime): Boolean {
        return (activeFrom == null || now.isAfter(activeFrom)) && (activeTo == null || now.isBefore(activeTo))
    }

}
