package pl.allegro.experiments.chi.chiserver.utils

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.application.statistics.ClassicStatisticsController

trait ApiActionUtils {
    String CLIENT_API_PATH = "/api/experiments"
    String ADMIN_API_PATH = "/api/admin/experiments"

    RestTemplate restTemplate = new RestTemplate()

    String localUrl(String endpoint) {
        return "http://localhost:$port$endpoint"
    }

    ResponseEntity get(String endpoint, Class type = List) {
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

    String createExperimentTag(String experimentTagId = null) {
        if (experimentTagId == null) {
            experimentTagId = UUID.randomUUID().toString().substring(0, 10)
        }
        post("/api/admin/experiments/tags", [experimentTagId: experimentTagId])

        experimentTagId
    }

    List fetchAllExperimentTags() {
        get('/api/admin/experiments/tags').body as List
    }

    ResponseEntity postClassicStatistics(Map statistics) {
        post("/api/statistics/", prepareStatisticsRequest(statistics))
    }

    ResponseEntity postBayesianStatistics(Map statistics) {
        post("/api/bayes/statistics/", prepareStatisticsRequest(statistics))
    }

    HttpEntity prepareStatisticsRequest(Map statistics) {
        HttpHeaders headers = new HttpHeaders()
        headers.set("Chi-Token", ClassicStatisticsController.CHI_TOKEN)
        new HttpEntity<>(statistics, headers)
    }

    List fetchStatistics(String experimentId) {
        get("/api/admin/statistics/$experimentId", List).body as List
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

    void removeFromGroup(String experimentId) {
        put("$ADMIN_API_PATH/$experimentId/remove-from-group")
    }

    void updateExperimentEventDefinitions(String experimentId, List eventDefinitions) {
        put("$ADMIN_API_PATH/$experimentId/update-event-definitions", eventDefinitions)
    }

    void updateExperimentDescriptions(String experimentId, String description, String documentLink, List groups, List tags) {
        def properties = [
                description: description,
                documentLink: documentLink,
                groups: groups,
                tags: tags
        ]
        put("$ADMIN_API_PATH/$experimentId/update-descriptions", properties)
    }

    void updateExperimentVariants(String experimentId,
                                  String internalVariantName,
                                  int percentage,
                                  String deviceClass) {
        def properties = [
                internalVariantName: internalVariantName,
                percentage: percentage,
                deviceClass: deviceClass
        ]
        put("$ADMIN_API_PATH/$experimentId/update-variants", properties)
    }

    List fetchExperimentsInfo() {
        get("/api/experiments-info", List).body as List
    }
}
