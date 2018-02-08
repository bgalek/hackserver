package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentId
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter


class StartExperimentCommand(private val experimentsRepository: ExperimentsRepository,
                             private val startExperimentProperties: StartExperimentProperties,
                             private val permissionsAwareExperimentGetter: PermissionsAwareExperimentGetter,
                             private val experimentId: ExperimentId) {

    fun execute() {
        val experiment = permissionsAwareExperimentGetter.getExperimentOrException(experimentId)
        validate(experiment)
        experimentsRepository.save(experiment.start(startExperimentProperties.experimentDurationDays))
    }

    private fun validate(experiment: Experiment) {
        if (!experiment.isDraft()) {
            throw StartExperimentException("Experiment is not DRAFT: ${experimentId}")
        }

        if (startExperimentProperties.experimentDurationDays <= 0) {
            throw StartExperimentException("Experiment duration days must be greater than 0: ${experimentId}")
        }
    }
}