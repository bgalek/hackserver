package pl.allegro.experiments.chi.chiserver.utils

import org.springframework.beans.factory.annotation.Autowired
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository

import static SampleExperimentRequests.sampleExperimentCreationRequest

trait CommandExperimentUtils implements CommandActionUtils {

    @Autowired
    ExperimentsRepository experimentsRepository

    boolean experimentsExists(String experimentId) {
        experimentsRepository.getExperiment(experimentId).isPresent()
    }

    Experiment fetchExperiment(String experimentId) {
        experimentsRepository.getExperiment(experimentId).get()
    }

    ExperimentDefinition fetchExperimentDefinition(String experimentId) {
        fetchExperiment(experimentId).definition.get()
    }

    Experiment draftExperiment(Map customProperties = [:]) {
        def request = sampleExperimentCreationRequest(customProperties)
        createExperiment(request)
        fetchExperiment(request.id)
    }

    Experiment startedExperiment(Map customProperties = [:], long experimentDurationDays = 14) {
        def experiment = draftExperiment(customProperties)
        startExperiment(experiment.id, experimentDurationDays)
        fetchExperiment(experiment.id)
    }

    Experiment pausedExperiment(Map customProperties = [:]) {
        def experiment = startedExperiment(customProperties)
        pauseExperiment(experiment.id)
        fetchExperiment(experiment.id)
    }

    Experiment fullOnExperiment(Map customProperties = [:], String fullOnVariantName = "v1") {
        def experiment = startedExperiment(customProperties)
        makeExperimentFullOn(experiment.id, fullOnVariantName)
        fetchExperiment(experiment.id)
    }

    Experiment endedExperiment(Map customProperties = [:]) {
        def experiment = startedExperiment(customProperties)
        stopExperiment(experiment.id)
        fetchExperiment(experiment.id)
    }

    Experiment experimentWithStatus(ExperimentStatus status, Map customProperties = [:]) {
        switch (status) {
            case ExperimentStatus.DRAFT:
                return draftExperiment(customProperties)
            case ExperimentStatus.ACTIVE:
                return startedExperiment(customProperties)
            case ExperimentStatus.ENDED:
                return endedExperiment(customProperties)
            case ExperimentStatus.PAUSED:
                return pausedExperiment(customProperties)
            case ExperimentStatus.FULL_ON:
                return fullOnExperiment(customProperties)
        }
    }
}
