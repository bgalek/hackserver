package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create

import pl.allegro.experiments.chi.chiserver.domain.experiments.*
import java.util.regex.Pattern

data class ExperimentCreationRequest (var id: ExperimentId,
                                      var variants: List<Variant>,
                                      var description: String? = null,
                                      var documentLink: String? = null,
                                      var groups: List<String> = emptyList(),
                                      var reportingEnabled: Boolean = false) {
    data class Variant (val name: String,
                   val predicates: List<Predicate>)

    enum class PredicateType {
        INTERNAL, HASH, CMUID_REGEXP, DEVICE_CLASS
    }

    data class Predicate (val type: PredicateType,
                          val from: Int? = null,
                          val to: Int? = null,
                          val regexp: String? = null,
                          val device: String? = null)

    fun toExperiment(author: String) : Experiment {
        try {
            return Experiment(
                    this.id,
                    this.variants.map { convertVariant(it) },
                    this.description,
                    this.documentLink,
                    author,
                    this.groups,
                    this.reportingEnabled)
        } catch (e: Exception) {
            throw ExperimentCreationException("Cannot create experiment from request $this", e)
        }
    }

    private fun convertVariant(variant: Variant): ExperimentVariant {
        return ExperimentVariant(variant.name, variant.predicates.map { convertPredicate(it) })
    }

    private fun convertPredicate(predicate: Predicate): pl.allegro.experiments.chi.chiserver.domain.experiments.Predicate {
        return when (predicate.type) {
            PredicateType.INTERNAL -> InternalPredicate()
            PredicateType.CMUID_REGEXP -> CmuidRegexpPredicate(Pattern.compile(predicate.regexp))
            PredicateType.HASH -> HashRangePredicate(PercentageRange(predicate.from!!, predicate.to!!))
            PredicateType.DEVICE_CLASS -> DeviceClassPredicate(predicate.device!!)
        }
    }


}
