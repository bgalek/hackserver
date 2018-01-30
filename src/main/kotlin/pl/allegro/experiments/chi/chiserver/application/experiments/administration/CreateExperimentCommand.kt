package pl.allegro.experiments.chi.chiserver.application.experiments.administration

import org.springframework.stereotype.Component
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.*
import pl.allegro.experiments.chi.chiserver.logger
import java.util.regex.Pattern

@Component
class CreateExperimentCommand(val experimentsRepository: ExperimentsRepository,
                              val userProvider: UserProvider) {

    companion object {
        private val logger by logger()
    }

    fun createExperiment(request: ExperimentCreationRequest)  {
        val user = userProvider.getCurrentUser()
        if (!user.isRoot) {
            throw AuthorizationException("User ${user.name} cannot create experiments")
        }
        if (experimentsRepository.getExperiment(request.id) != null) {
            throw ExperimentCreationException("Experiment with id ${request.id} already exists")
        }

        experimentsRepository.save(convert(request, user.name))
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
            throw ExperimentCreationException("Cannot create experiment from request $request")
        }
    }

    private fun convertVariant(variant: ExperimentCreationRequest.Variant): ExperimentVariant {
        return ExperimentVariant(variant.name, variant.predicates.map { convertPredicate(it) })
    }

    private fun convertPredicate(predicate: ExperimentCreationRequest.Predicate): Predicate {
        return when (predicate.type) {
            ExperimentCreationRequest.PredicateType.INTERNAL -> InternalPredicate()
            ExperimentCreationRequest.PredicateType.CMUID_REGEXP -> CmuidRegexpPredicate(Pattern.compile(predicate.cmuidRegexp))
            ExperimentCreationRequest.PredicateType.HASH -> HashRangePredicate(PercentageRange(predicate.hashRangeFrom!!, predicate.hashRangeTo!!))
        }
    }
}
