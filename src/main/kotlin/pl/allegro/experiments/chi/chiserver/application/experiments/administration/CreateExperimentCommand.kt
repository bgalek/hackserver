package pl.allegro.experiments.chi.chiserver.application.experiments.administration

import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.*
import pl.allegro.experiments.chi.chiserver.logger
import java.util.regex.Pattern

class CreateExperimentCommand(val experimentsRepository: ExperimentsRepository,
                              val userProvider: UserProvider,
                              val experimentCreationRequest: ExperimentCreationRequest) {

    companion object {
        private val logger by logger()
    }

    fun execute() {
        val user = userProvider.getCurrentUser()
        if (!user.isRoot) {
            throw AuthorizationException("User ${user.name} cannot create experiments")
        }
        if (experimentsRepository.getExperiment(experimentCreationRequest.id) != null) {
            throw ExperimentCreationException("Experiment with id ${experimentCreationRequest.id} already exists")
        }

        experimentsRepository.save(convert(experimentCreationRequest, user.name))
    }

    private fun convert(request: ExperimentCreationRequest, author: String) : Experiment {
        try {
            return Experiment(
                    request.id,
                    request.variants.map { convertVariant(it) },
                    request.description,
                    author,
                    request.groups,
                    request.reportingEnabled)
        } catch (e: Exception) {
            throw ExperimentCreationException("Cannot create experiment from request $request", e)
        }
    }

    private fun convertVariant(variant: ExperimentCreationRequest.Variant): ExperimentVariant {
        return ExperimentVariant(variant.name, variant.predicates.map { convertPredicate(it) })
    }

    private fun convertPredicate(predicate: ExperimentCreationRequest.Predicate): Predicate {
        return when (predicate.type) {
            ExperimentCreationRequest.PredicateType.INTERNAL -> InternalPredicate()
            ExperimentCreationRequest.PredicateType.CMUID_REGEXP -> CmuidRegexpPredicate(Pattern.compile(predicate.regexp))
            ExperimentCreationRequest.PredicateType.HASH -> HashRangePredicate(PercentageRange(predicate.from!!, predicate.to!!))
        }
    }
}
