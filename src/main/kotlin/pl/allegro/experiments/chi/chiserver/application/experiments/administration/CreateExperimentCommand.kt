package pl.allegro.experiments.chi.chiserver.application.experiments.administration

import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCreationException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCreationRequest
import pl.allegro.experiments.chi.chiserver.logger

class CreateExperimentCommand(private val experimentsRepository: ExperimentsRepository,
                              private val userProvider: UserProvider,
                              private val experimentCreationRequest: ExperimentCreationRequest) {

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

        experimentsRepository.save((experimentCreationRequest.toExperiment(user.name)))
    }
}
