package pl.allegro.experiments.chi.chiserver.utils

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus

import static SampleExperimentRequests.sampleExperimentCreationRequest

trait CommandExperimentUtils implements CommandActionUtils {

    boolean experimentsExists(String experimentId) {
        experimentsRepository.getExperiment(experimentId).isPresent()
    }

    ExperimentDefinition fetchExperiment(String experimentId) {
        experimentsRepository.getExperiment(experimentId).get()
    }

    ExperimentDefinition draftExperiment(Map customProperties = [:]) {
        def request = sampleExperimentCreationRequest(customProperties)
        createExperiment(request)
        fetchExperiment(request.id)
    }

    ExperimentDefinition startedExperiment(Map customProperties = [:], long experimentDurationDays = 14) {
        def experiment = draftExperiment(customProperties)
        startExperiment(experiment.id, experimentDurationDays)
        fetchExperiment(experiment.id)
    }

    ExperimentDefinition pausedExperiment(Map customProperties = [:]) {
        def experiment = startedExperiment(customProperties)
        pauseExperiment(experiment.id)
        fetchExperiment(experiment.id)
    }

    ExperimentDefinition fullOnExperiment(Map customProperties = [:], String fullOnVariantName = "v1") {
        def experiment = startedExperiment(customProperties)
        makeExperimentFullOn(experiment.id, fullOnVariantName)
        fetchExperiment(experiment.id)
    }

    ExperimentDefinition endedExperiment(Map customProperties = [:]) {
        def experiment = startedExperiment(customProperties)
        stopExperiment(experiment.id)
        fetchExperiment(experiment.id)
    }

    ExperimentDefinition experimentWithStatus(ExperimentStatus status, Map customProperties = [:]) {
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
