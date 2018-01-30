package pl.allegro.experiments.chi.chiserver.application.experiments.administration

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentId

data class ExperimentCreationRequest (var id: ExperimentId,
                                      var variants: List<Variant>,
                                      var description: String? = null,
                                      var groups: List<String> = emptyList(),
                                      var reportingEnabled: Boolean = false) {
    class Variant (val name: String,
                   val predicates: List<Predicate>)

    enum class PredicateType {
        INTERNAL, HASH, CMUID_REGEXP
    }

    class Predicate (val type: PredicateType,
                     val from: Int? = null,
                     val to: Int? = null,
                     val regexp: String? = null)

}
