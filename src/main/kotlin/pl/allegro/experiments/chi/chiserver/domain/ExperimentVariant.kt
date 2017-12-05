package pl.allegro.experiments.chi.chiserver.domain

import com.google.common.base.Preconditions
import com.google.common.base.Strings
import java.util.*

class ExperimentVariant(val name: String, private val predicates: List<Predicate>) {

    init {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "empty name in ExperimentVariant")
    }

    fun getPredicates(): List<Predicate> {
        return Collections.unmodifiableList(predicates)
    }
}
