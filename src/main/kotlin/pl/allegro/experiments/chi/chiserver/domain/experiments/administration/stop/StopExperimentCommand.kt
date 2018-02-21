package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.stop

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter

class StopExperimentCommand(
        private val experimentId: String,
        private val experimentsRepository: ExperimentsRepository,
        private val permissionsAwareExperimentGetter: PermissionsAwareExperimentGetter
) {

    fun execute() {
        val experiment = permissionsAwareExperimentGetter.getExperimentOrException(experimentId)
        validate(experiment)
        val stoppedExperiment = experiment.stop()
        experimentsRepository.save(stoppedExperiment)
    }

    private fun validate(experiment: Experiment) {
        if (!experiment.isActive()) {
            throw StopExperimentException("Experiment is not ACTIVE: ${experimentId}")
        }
    }
}