package pl.allegro.experiments.chi.chiserver.utils

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus

import static pl.allegro.experiments.chi.chiserver.utils.SampleExperimentRequests.sampleExperimentCreationRequestProperties

trait ApiExperimentUtils implements ApiActionUtils {

    Map fetchClientExperiment(String expId) {
        get("$CLIENT_API_PATH", List).body.find { it.id == expId }
    }

    List fetchClientExperiments(String apiVersion = "") {
        get("$CLIENT_API_PATH/$apiVersion", List).body as List
    }

    List fetchExperiments() {
        get(ADMIN_API_PATH, List).body as List
    }

    Map fetchExperiment(String experimentId) {
        get("$ADMIN_API_PATH/$experimentId", Map).body as Map
    }

    Map fetchExperimentGroup(String groupId) {
        get("$ADMIN_API_PATH/groups/$groupId", Map).body as Map
    }

    @Deprecated
    Map addToGroupAndFetch(String experimentId, String groupId = UUID.randomUUID().toString()) {
        addToGroup(experimentId, groupId)
        fetchExperimentGroup(groupId)
    }

    @Deprecated
    Map experimentGroup(List experimentIds, String groupId = UUID.randomUUID().toString()) {
        createExperimentGroup(experimentIds, groupId)
        fetchExperimentGroup(groupId)
    }

    String createExperimentGroup(List<String> ids) {
        def groupName = UUID.randomUUID().toString()
        ids.each{ addToGroup(it, groupName) }
        groupName
    }

    Map createExperimentGroupAndFetch(List<String> ids) {
        def groupName = UUID.randomUUID().toString()
        ids.each{ addToGroup(it, groupName) }
        fetchExperimentGroup(groupName)
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