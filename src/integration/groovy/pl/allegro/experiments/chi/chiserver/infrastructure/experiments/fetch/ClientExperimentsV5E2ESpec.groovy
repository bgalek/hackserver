package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.CachedExperimentGroupRepository
import spock.lang.Unroll

class ClientExperimentsV5E2ESpec extends BaseIntegrationSpec {

    @Autowired
    ExperimentGroupRepository experimentGroupRepository

    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    UserProvider userProvider

    def setup() {
        if (!experimentGroupRepository instanceof CachedExperimentGroupRepository) {
            throw new RuntimeException("We should test cached repository")
        }
        ResponseEntity.metaClass.numberOfExperimentsPresent << { List<String> experimentIds ->
            getBody().stream()
                    .filter({e -> e.id in experimentIds})
                    .count()
        }
        userProvider.user = new User('Author', [], true)
    }

    def "should ignore full-on experiments in client api v4"() {
        given:
        def draftExperimentId = createDraftExperiment(['base', 'v1'])
        def startedExperimentId = createStartedExperiment(['base', 'v1'])
        def fullOnExperimentId = createFullOnExperiment(['base', 'v1'], 'v1')
        def experiments = [draftExperimentId, startedExperimentId, fullOnExperimentId]

        when:
        def response = restTemplate.getForEntity(localUrl("/api/experiments/v4"), List)

        then:
        response.numberOfExperimentsPresent(experiments) == 2
        def presentIds = response.body.collect{it.id}
        presentIds.contains(draftExperimentId)
        presentIds.contains(startedExperimentId)
        !presentIds.contains(fullOnExperimentId)
    }

    def "should return full-on experiment in client api v5"() {
        given:
        createFullOnExperiment(['base', 'v1'], 'v1', "full-on_exp")

        when:
        def response = restTemplate.getForEntity(localUrl("/api/experiments/v5"), List)

        then:
        response.body.contains([
            id: "full-on_exp",
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
    }

    def createDraftExperiment(List<String> variants, String id = UUID.randomUUID().toString()) {
        def request = [
                id                 : id,
                description        : 'desc',
                documentLink       : 'https://vuetifyjs.com/vuetify/quick-start',
                variantNames       : variants,
                internalVariantName: 'v3',
                percentage         : 10,
                deviceClass        : 'phone',
                groups             : ['group a', 'group b'],
                reportingEnabled   : true,
                reportingType: 'BACKEND'
        ]
        restTemplate.postForEntity(localUrl('/api/admin/experiments'), request, Map)
        return id
    }

    def createStartedExperiment(List<String> variants, String id = UUID.randomUUID().toString()) {
        def experimentId = createDraftExperiment(variants, id)
        startExperiment(experimentId)
        return experimentId
    }

    def createFullOnExperiment(List<String> variants, String variant, String id = UUID.randomUUID().toString()) {
        def experimentId = createStartedExperiment(variants, id)
        makeExperimentFullOn(experimentId, variant)
        return experimentId
    }

    def makeExperimentFullOn(String experimentId, String variant) {
        def request = [
                variantName: variant
        ]
        restTemplate.put(localUrl("/api/admin/experiments/${experimentId}/full-on"), request, Map)
    }
}