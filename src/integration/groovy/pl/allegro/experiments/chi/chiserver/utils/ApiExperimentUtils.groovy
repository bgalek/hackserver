package pl.allegro.experiments.chi.chiserver.utils

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository

import static pl.allegro.experiments.chi.chiserver.utils.SampleExperimentRequests.sampleExperimentCreationRequest
import static pl.allegro.experiments.chi.chiserver.utils.SampleExperimentRequests.sampleExperimentCreationRequestProperties

trait ApiExperimentUtils implements ApiActionUtils {

    boolean experimentsExists(String experimentId) {
        get(experimentId).statusCode != HttpStatus.NOT_FOUND
    }

    List fetchExperiments(String apiVersion = "") {
        get(apiVersion, List).body as List
    }

    Map fetchExperiment(String experimentId) {
        get(experimentId, Map).body as Map
    }

    Map draftExperiment(Map customProperties = [:]) {
        def request = sampleExperimentCreationRequestProperties(customProperties)
        createExperiment(request)
        fetchExperiment(request.id as String)
    }

    Map startedExperiment(Map customProperties = [:], long experimentDurationDays = 14) {
        def experiment = draftExperiment(customProperties)
        startExperiment(experiment.id as String, experimentDurationDays)
        fetchExperiment(experiment.id as String)
    }

    Map pausedExperiment(Map customProperties = [:]) {
        def experiment = startedExperiment(customProperties)
        pauseExperiment(experiment.id as String)
        fetchExperiment(experiment.id as String)
    }

    Map fullOnExperiment(Map customProperties = [:], String fullOnVariantName = "v1") {
        def experiment = startedExperiment(customProperties)
        makeExperimentFullOn(experiment.id as String, fullOnVariantName)
        fetchExperiment(experiment.id as String)
    }

    Map endedExperiment(Map customProperties = [:]) {
        def experiment = startedExperiment(customProperties)
        stopExperiment(experiment.id as String)
        fetchExperiment(experiment.id as String)
    }

    Map experimentWithStatus(ExperimentStatus status, Map customProperties = [:]) {
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