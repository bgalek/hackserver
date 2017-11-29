package pl.allegro.experiments.chi.chiserver.domain

import com.google.common.base.Preconditions
import com.google.common.base.Strings
import java.util.*

class ExperimentVariant internal constructor(val name: String, private val predicates: List<Predicate>) {

    fun getPredicates(): List<Predicate> {
        return Collections.unmodifiableList(predicates)
    }

    init {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "empty name in ExperimentVariant")
    }
}
