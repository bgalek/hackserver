package pl.allegro.experiments.chi.chiserver.utils

import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicExperimentStatistics

trait ApiActionUtils {
    String CLIENT_API_PATH = "/api/experiments"
    String ADMIN_API_PATH = "/api/admin/experiments"

    RestTemplate restTemplate = new RestTemplate()

    String localUrl(String endpoint) {
        return "http://localhost:$port$endpoint"
    }

    ResponseEntity get(String endpoint, Class type = Map) {
        restTemplate.getForEntity(localUrl(endpoint), type)
    }

    ResponseEntity post(String endpoint, def request = null, Class type = Map) {
        restTemplate.postForEntity(localUrl(endpoint), request, type)
    }

    void put(String endpoint, def request = null) {
        restTemplate.put(localUrl(endpoint), request)
    }

    void delete(String endpoint) {
        restTemplate.delete(localUrl(endpoint))
    }

    void createExperiment(Map request) {
        post(ADMIN_API_PATH, request)
    }

    void postInteractions(List interactions, String apiVersion = "") {
        post("/api/interactions/$apiVersion", interactions, List)
    }

    ResponseEntity postClassicStatistics(Map statistics) {
        post("/api/statistics/", statistics)
    }

    ResponseEntity postBayesianStatistics(Map statistics) {
        post("/api/bayes/statistics/", statistics)
    }

    Map fetchStatistics(String experimentId) {
        get("/api/statistics/$experimentId", Map).body as Map
    }

    void startExperiment(String experimentId, long experimentDurationDays) {
        def properties = [experimentDurationDays: experimentDurationDays]
        put("$ADMIN_API_PATH/$experimentId/start", properties)
    }

    void prolongExperiment(String experimentId, long experimentAdditionalDays) {
        def properties = [experimentAdditionalDays: experimentAdditionalDays]
        put("$ADMIN_API_PATH/$experimentId/prolong", properties)
    }

    void pauseExperiment(String experimentId) {
        put("$ADMIN_API_PATH/$experimentId/pause")
    }

    void resumeExperiment(String experimentId) {
        put("$ADMIN_API_PATH/$experimentId/resume")
    }

    void makeExperimentFullOn(String experimentId, String fullOnVariantName) {
        def properties = [variantName: fullOnVariantName]
        put("$ADMIN_API_PATH/$experimentId/full-on", properties)
    }

    void stopExperiment(String experimentId) {
        put("$ADMIN_API_PATH/$experimentId/stop")
    }

    void deleteExperiment(String experimentId) {
        delete("$ADMIN_API_PATH/$experimentId")
    }

    void addToGroup(String experimentId, String groupId = UUID.randomUUID().toString()) {
        def request = [id: groupId, experiment: experimentId]
        post("$ADMIN_API_PATH/groups/", request)
    }

    void createPairedExperiment(Map experimentRequest, List<String> experimentIds, String groupId = UUID.randomUUID().toString()) {
        def groupRequest = [id: groupId, experiments: experimentIds + [experimentRequest.id]]
        def request = [experimentCreationRequest: experimentRequest, experimentGroupCreationRequest: groupRequest]
        post ("$ADMIN_API_PATH/create-paired-experiment", request)
    }

    void updateExperimentEventDefinitions(String experimentId, List eventDefinitions) {
        put("$ADMIN_API_PATH/$experimentId/update-event-definitions", eventDefinitions)
    }

    void updateExperimentDescriptions(String experimentId, String description, String documentLink, List groups) {
        def properties = [
                description: description,
                documentLink: documentLink,
                groups: groups
        ]
        put("$ADMIN_API_PATH/$experimentId/update-descriptions", properties)
    }

    void updateExperimentVariants(String experimentId,
                                  List variantNames,
                                  String internalVariantName,
                                  int percentage,
                                  String deviceClass) {
        def properties = [
                variantNames: variantNames,
                internalVariantName: internalVariantName,
                percentage: percentage,
                deviceClass: deviceClass
        ]
        put("$ADMIN_API_PATH/$experimentId/update-variants", properties)
    }
}
