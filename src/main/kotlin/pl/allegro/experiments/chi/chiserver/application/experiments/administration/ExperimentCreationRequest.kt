package pl.allegro.experiments.chi.chiserver.application.experiments.administration

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentId

data class ExperimentCreationRequest (var id: ExperimentId,
                                      var variants: List<VariantDTO>,
                                      var description: String? = null,
                                      var groups: List<String> = emptyList(),
                                      var reportingEnabled: Boolean = false)

class VariantDTO (val name: String,
                  val predicates: List<PredicateDTO>)

enum class PredicateType {
    INTERNAL, HASH, CMUID_REGEXP
}

class PredicateDTO (val type: PredicateType,
                    val hashRangeFrom: Int?,
                    val hashRangeTo: Int?,
                    val cmuidRegexp: String?)
