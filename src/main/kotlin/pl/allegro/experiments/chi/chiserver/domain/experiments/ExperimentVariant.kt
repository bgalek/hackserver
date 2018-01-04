package pl.allegro.experiments.chi.chiserver.domain.experiments

import java.util.*

class ExperimentVariant(val name: String, private val predicates: List<Predicate>) {

    init {
        require(name.isNotBlank()) { "empty name in ExperimentVariant" }
    }

    fun getPredicates(): List<Predicate> {
        return Collections.unmodifiableList(predicates)
    }
}
