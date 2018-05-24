package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.ClassRule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.FileBasedExperimentsRepository
import spock.lang.Shared
import spock.lang.Unroll

class ClientExperimentsV3E2ESpec extends BaseIntegrationSpec {

    @ClassRule
    @Shared
    public WireMockRule wireMock = new WireMockRule(port)

    @Autowired
    ExperimentGroupRepository experimentGroupRepository

    @Autowired
    FileBasedExperimentsRepository fileBasedExperimentsRepository

    @Autowired
    ExperimentsRepository experimentsRepository

    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    UserProvider userProvider

    @Unroll
    def "should ignore grouped experiments in client api version #apiVersion"() {
        given:
        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        and:
        String experimentId1 = UUID.randomUUID().toString()
        String experimentId2 = UUID.randomUUID().toString()
        def experiments = [experimentId1, experimentId2]
        createDraftExperiment(experimentId1, ['base', 'v1'])
        createDraftExperiment(experimentId2, ['base', 'v2'])

        and:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId,
                experiments: [experimentId1, experimentId2]
        ], Map)

        and:
        startExperiment(experimentId1)
        startExperiment(experimentId2)

        when:
        def response = restTemplate.getForEntity(localUrl("/api/experiments/${apiVersion}"), List)

        then:
        !response.body.stream()
                .filter({e -> e.id in experiments})
                .findAny()
                .isPresent()

        where:
        apiVersion << ['v1', 'v2']
    }

    def "should return grouped experiments in v3"() {
        given:
        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        and:
        String experimentId1 = UUID.randomUUID().toString()
        String experimentId2 = UUID.randomUUID().toString()
        def experiments = [experimentId1, experimentId2]
        createDraftExperiment(experimentId1, ['base', 'v1'])
        createDraftExperiment(experimentId2, ['base', 'v2'])
        startExperiment(experimentId1)

        and:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId,
                experiments: [experimentId1, experimentId2]
        ], Map)

        and:
        startExperiment(experimentId2)

        when:
        def response = restTemplate.getForEntity(localUrl("/api/experiments/v3"), List)

        then:
        response.body.stream()
                .filter({e -> e.id in experiments})
                .count() == 2

        and:
        response.body.find {e -> e.id == experimentId1}.variants == [
                [
                        name: 'base',
                        predicates: [
                            [
                                    type: 'SHRED_HASH',
                                    ranges: [[from: 90, to: 100]],
                                    salt: experimentId1
                            ]
                        ]
                ],
                [
                        name: 'v1',
                        predicates: [
                                [
                                        type: 'SHRED_HASH',
                                        ranges: [[from: 0, to: 10]],
                                        salt: experimentId1
                                ]
                        ]
                ]
        ]

        and:
        response.body.find {e -> e.id == experimentId2}.variants == [
                [
                        name: 'base',
                        predicates: [
                                [
                                        type: 'SHRED_HASH',
                                        ranges: [[from: 90, to: 100]],
                                        salt: experimentId1
                                ]
                        ]
                ],
                [
                        name: 'v2',
                        predicates: [
                                [
                                        type: 'SHRED_HASH',
                                        ranges: [[from: 10, to: 20]],
                                        salt: experimentId1
                                ]
                        ]
                ]
        ]

    }

    // todo remove duplication
    def startExperiment(String experimentId) {
        def startRequest = [
                experimentDurationDays: 30
        ]
        restTemplate.put(localUrl("/api/admin/experiments/${experimentId}/start"), startRequest, Map)
    }

    def createDraftExperiment(String experimentId, List<String> variants, int percentage=10) {
        def request = [
                id                 : experimentId,
                description        : 'desc',
                documentLink       : 'https://vuetifyjs.com/vuetify/quick-start',
                variantNames       : variants,
                internalVariantName: 'v3',
                percentage         : percentage,
                deviceClass        : 'phone',
                groups             : ['group a', 'group b'],
                reportingEnabled   : true,
                reportingType: 'BACKEND'
        ]
        restTemplate.postForEntity(localUrl('/api/admin/experiments'), request, Map)
    }
}