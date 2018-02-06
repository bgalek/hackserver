package pl.allegro.experiments.chi.chiserver.domain.experiments.administration

import pl.allegro.experiments.chi.chiserver.application.experiments.administration.AuthorizationException
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.logger


class StartExperimentCommand(private val experimentsRepository: ExperimentsRepository,
                              private val userProvider: UserProvider,
                              private val startExperimentRequest: StartExperimentRequest) {

    companion object {
        private val logger by logger()
    }

    fun execute() {
        val experiment = experimentsRepository.getExperiment(startExperimentRequest.experimentId)
        validate(experiment)
        experimentsRepository.save(experiment!!.start(startExperimentRequest.experimentDurationDays))
    }

    private fun validate(experiment: Experiment?) {
        if (experiment == null) {
            throw ExperimentNotFoundException("Experiment not found: " + startExperimentRequest.experimentId)
        }
        val user = userProvider.getCurrentUser()
        if (!user.canEdit(experiment) && !user.isRoot) {
            throw AuthorizationException("User has no permission to edit experiment: " + startExperimentRequest.experimentId)
        }

        if (!experiment.isDraft()) {
            throw StartExperimentException("Experiment is not DRAFT: " + startExperimentRequest.experimentId)
        }
    }
}