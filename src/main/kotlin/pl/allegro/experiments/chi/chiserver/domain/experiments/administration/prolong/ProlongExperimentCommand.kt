package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter

class ProlongExperimentCommand(
        private val experimentsRepository: ExperimentsRepository,
        private val prolongExperimentProperties: ProlongExperimentProperties,
        private val permissionsAwareExperimentGetter: PermissionsAwareExperimentGetter,
        private val experimentId: String
) {
    fun execute() {
        val experiment = permissionsAwareExperimentGetter.getExperimentOrException(experimentId)
        validate(experiment)
        experimentsRepository.save(experiment.prolong(prolongExperimentProperties.experimentAdditionalDays))
    }

    private fun validate(experiment: Experiment) {
        if (!experiment.isActive()) {
            throw ProlongExperimentException("Experiment is not ACTIVE: ${experimentId}")
        }
        if (prolongExperimentProperties.experimentAdditionalDays <= 0) {
            throw ProlongExperimentException("Experiment additional days must be greater than 0: ${experimentId}")
        }
    }
}