package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import spock.lang.Unroll

class ClientExperimentsV5E2ESpec extends BaseIntegrationSpec {

    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    UserProvider userProvider

    def setup() {
        userProvider.user = new User('Author', [], true)
    }

    @Unroll
    def "should ignore full-on experiments in client API #apiVersion"() {
        given:
        def draftExperimentId = createDraftExperiment(['base', 'v1'])
        def startedExperimentId = createStartedExperiment(['base', 'v1'])
        def fullOnExperimentId = createFullOnExperiment(['base', 'v1'], 'v1')

        when:
        def response = restTemplate.getForEntity(localUrl("/api/experiments/$apiVersion"), List)

        then:
        def presentIds = response.body.collect{it.id}
        presentIds.contains(draftExperimentId)
        presentIds.contains(startedExperimentId)
        !presentIds.contains(fullOnExperimentId)

        where:
        apiVersion << ["v1","v2","v3","v4"]
    }

    @Unroll
    def "should return full-on experiment in client API #apiDesc"() {
        given:
        def expId = createFullOnExperiment(['base', 'v1'], 'v1')

        when:
        def response = restTemplate.getForEntity(localUrl("/api/experiments/$apiVersion"), List)

        then:
        response.body.contains([
            id: expId,
            status: "FULL_ON",
            reportingEnabled: true,
            variants: [
                [
                    name: "v1",
                    predicates: [
                        [
                            type: "FULL_ON"
                        ]
                    ]
                ]
            ]
        ])

        where:
        apiVersion << ["v5", ""]
        apiDesc <<    ["v5", "latest"]
    }

    String createDraftExperiment(List<String> variants) {
        def expId = UUID.randomUUID().toString()
        def request = [
                id                 : expId,
                description        : 'desc',
                documentLink       : 'https://vuetifyjs.com/vuetify/quick-start',
                variantNames       : variants,
                internalVariantName: 'v3',
                percentage         : 10,
                groups             : ['group a', 'group b'],
                reportingEnabled   : true,
                reportingType: 'BACKEND'
        ]
        restTemplate.postForEntity(localUrl('/api/admin/experiments'), request, Map)
        expId
    }

    String createStartedExperiment(List<String> variants) {
        def experimentId = createDraftExperiment(variants)
        startExperiment(experimentId)
        experimentId
    }

    String createFullOnExperiment(List<String> variants, String variant) {
        def experimentId = createStartedExperiment(variants)
        makeExperimentFullOn(experimentId, variant)
        experimentId
    }

    void makeExperimentFullOn(String experimentId, String variant) {
        def request = [
                variantName: variant
        ]
        restTemplate.put(localUrl("/api/admin/experiments/${experimentId}/full-on"), request, Map)
    }
}