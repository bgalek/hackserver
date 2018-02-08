package pl.allegro.experiments.chi.chiserver.domain.experiments.administration

import pl.allegro.experiments.chi.chiserver.application.experiments.administration.AuthorizationException
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentId
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository

class PermissionsAwareExperimentGetter(
        private val experimentsRepository: ExperimentsRepository,
        private val userProvider: UserProvider) {

    fun getExperimentOrException(id: ExperimentId): Experiment {
        val experiment = experimentsRepository.getExperiment(id)
        if (experiment == null) {
            throw ExperimentNotFoundException("Experiment not found: ${id}")
        }

        val user = userProvider.getCurrentUser()
        if (!user.isOwner(experiment)) {
            throw AuthorizationException("User has no permission to edit experiment: ${id}")
        }

        return experiment
    }
}