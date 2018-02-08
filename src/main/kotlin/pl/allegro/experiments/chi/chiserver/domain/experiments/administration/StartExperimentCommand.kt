package pl.allegro.experiments.chi.chiserver.domain.experiments.administration

import pl.allegro.experiments.chi.chiserver.application.experiments.administration.AuthorizationException
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentId
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository


class StartExperimentCommand(private val experimentsRepository: ExperimentsRepository,
                             private val userProvider: UserProvider,
                             private val startExperimentProperties: StartExperimentProperties,
                             private val experimentId: ExperimentId) {

    fun execute() {
        val experiment = experimentsRepository.getExperiment(experimentId)
        validate(experiment)
        experimentsRepository.save(experiment!!.start(startExperimentProperties.experimentDurationDays))
    }

    private fun validate(experiment: Experiment?) {
        if (experiment == null) {
            throw ExperimentNotFoundException("Experiment not found: ${experimentId}")
        }

        val user = userProvider.getCurrentUser()
        if (!user.isOwner(experiment) && !user.isRoot) {
            throw AuthorizationException("User has no permission to edit experiment: ${experimentId}")
        }

        if (!experiment.isDraft()) {
            throw StartExperimentException("Experiment is not DRAFT: ${experimentId}")
        }

        if (startExperimentProperties.experimentDurationDays <= 0) {
            throw StartExperimentException("Experiment duration days must be greater than 0: ${experimentId}")
        }
    }
}