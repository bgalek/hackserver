package pl.allegro.experiments.chi.chiserver.domain.experiments

class ExperimentVariant(val name: String, val predicates: List<Predicate>) {

    init {
        require(name.isNotBlank()) { "empty name in ExperimentVariant" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExperimentVariant

        if (name != other.name) return false
        if (predicates != other.predicates) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + predicates.hashCode()
        return result
    }
}
