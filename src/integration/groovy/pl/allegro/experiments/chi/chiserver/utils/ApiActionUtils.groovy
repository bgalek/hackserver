package pl.allegro.experiments.chi.chiserver.utils

import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

trait ApiActionUtils {

    String PATH_PREFIX = "/api/admin/experiments/"

    RestTemplate restTemplate = new RestTemplate()

    String localUrl(String endpoint) {
        return "http://localhost:$port$PATH_PREFIX$endpoint"
    }

    ResponseEntity get(String endpoint, Class type = Map) {
        restTemplate.getForEntity(localUrl(endpoint), type)
    }

    void post(String endpoint, def request = "") {
        restTemplate.postForEntity(localUrl(endpoint), request, Map)
    }

    void put(String endpoint, def request = "") {
        restTemplate.put(localUrl(endpoint), request)
    }

    void delete(String endpoint) {
        restTemplate.delete(localUrl(endpoint))
    }

    void createExperiment(Map request) {
        post("", request)
    }

    void startExperiment(String experimentId, long experimentDurationDays) {
        def properties = [experimentDurationDays: experimentDurationDays]
        put("$experimentId/start", properties)
    }

    void prolongExperiment(String experimentId, long experimentAdditionalDays) {
        def properties = [experimentAdditionalDays: experimentAdditionalDays]
        put("$experimentId/prolong", properties)
    }

    void pauseExperiment(String experimentId) {
        put("$experimentId/prolong")
    }

    void resumeExperiment(String experimentId) {
        put("$experimentId/resume")
    }

    void makeExperimentFullOn(String experimentId, String fullOnVariantName) {
        def properties = [variantName: fullOnVariantName]
        put("$experimentId/full-on", properties)
    }

    void stopExperiment(String experimentId) {
        put("$experimentId/stop")
    }

    void deleteExperiment(String experimentId) {
        delete(experimentId)
    }

    void createExperimentGroup(List<String> experimentIds, String groupId = UUID.randomUUID().toString()) {
        def request = [id: groupId, experimentIds: experimentIds]
        post("groups", request)
    }

    void updateExperimentEventDefinitions(String experimentId, List eventDefinitions) {
        put("$experimentId/update-event-definitions", eventDefinitions)
    }

    void updateExperimentVariants(String experimentId,
                                  List<String> variantNames,
                                  String internalVariantName,
                                  int percentage,
                                  String deviceClass) {
        def properties = [
                variantNames: variantNames,
                internalVariantName: internalVariantName,
                percentage: percentage,
                deviceClass: deviceClass
        ]
        put("$experimentId/update-variants", properties)
    }
}
