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

    def "should ignore PAUSED and ENDED experiments when rendering grouped experiments"() {
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

        and:
        restTemplate.put(localUrl("/api/admin/experiments/${experimentId1}/pause"), Map)

        and:
        restTemplate.put(localUrl("/api/admin/experiments/${experimentId2}/stop"), Map)

        when:
        def response = restTemplate.getForEntity(localUrl("/api/experiments/v3"), List)

        then:
        response.body.stream()
                .filter({e -> e.id in experiments})
                .count() == 0
    }

    def "should not ignore DRAFT experiments when rendering grouped experiments"() {
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
    }

    def "should render grouped experiments ranges in deterministic manner"() {
        given:
        String experiment1 = UUID.randomUUID().toString()
        String experiment2 = UUID.randomUUID().toString()
        String experiment3 = UUID.randomUUID().toString()
        String experiment4 = UUID.randomUUID().toString()

        and:
        def expectedExperiment1State = [
                [
                        name: 'base',
                        predicates: [
                                [
                                        type: 'SHRED_HASH',
                                        ranges: [[from: 95, to: 100]],
                                        salt: experiment1
                                ]
                        ]
                ],
                [
                        name: 'v1',
                        predicates: [
                                [
                                        type: 'SHRED_HASH',
                                        ranges: [[from: 0, to: 5]],
                                        salt: experiment1
                                ]
                        ]
                ]
        ]
        def expectedExperiment2State = [
                [
                        name: 'base',
                        predicates: [
                                [
                                        type: 'SHRED_HASH',
                                        ranges: [[from: 90, to: 100]],
                                        salt: experiment1
                                ]
                        ]
                ],
                [
                        name: 'v1',
                        predicates: [
                                [
                                        type: 'SHRED_HASH',
                                        ranges: [[from: 5, to: 15]],
                                        salt: experiment1
                                ]
                        ]
                ]
        ]

        def expectedExperiment3State = [
                [
                        name: 'base',
                        predicates: [
                                [
                                        type: 'SHRED_HASH',
                                        ranges: [[from: 90, to: 100]],
                                        salt: experiment1
                                ]
                        ]
                ],
                [
                        name: 'v1',
                        predicates: [
                                [
                                        type: 'SHRED_HASH',
                                        ranges: [[from: 15, to: 25]],
                                        salt: experiment1
                                ]
                        ]
                ]
        ]

        def expectedExperiment4State = [
                [
                        name: 'base',
                        predicates: [
                                [
                                        type: 'SHRED_HASH',
                                        ranges: [[from: 85, to: 100]],
                                        salt: experiment1
                                ]
                        ]
                ],
                [
                        name: 'v1',
                        predicates: [
                                [
                                        type: 'SHRED_HASH',
                                        ranges: [[from: 25, to: 40]],
                                        salt: experiment1
                                ]
                        ]
                ]
        ]

        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        and:
        def experiments = [experiment2, experiment3, experiment4, experiment1]
        createDraftExperiment(experiment1, ['base', 'v1'], 5)
        createDraftExperiment(experiment2, ['base', 'v1'], 10)
        createDraftExperiment(experiment3, ['base', 'v1'], 10)
        createDraftExperiment(experiment4, ['base', 'v1'], 15)

        startExperiment(experiment1)

        and:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId,
                experiments: experiments
        ], Map)

        when:
        def response = restTemplate.getForEntity(localUrl("/api/experiments/v3"), List)

        then:
        response.body.stream()
                .filter({e -> e.id in experiments})
                .count() == 4

        and:
        response.body.find {e -> e.id == experiment1}.variants == expectedExperiment1State

        when:
        startExperiment(experiment2)
        response = restTemplate.getForEntity(localUrl("/api/experiments/v3"), List)

        then:
        response.body.stream()
                .filter({e -> e.id in experiments})
                .count() == 4

        and:
        response.body.find {e -> e.id == experiment1}.variants == expectedExperiment1State
        response.body.find {e -> e.id == experiment2}.variants == expectedExperiment2State

        when:
        startExperiment(experiment3)
        response = restTemplate.getForEntity(localUrl("/api/experiments/v3"), List)

        then:
        response.body.stream()
                .filter({e -> e.id in experiments})
                .count() == 4

        and:
        response.body.find {e -> e.id == experiment1}.variants == expectedExperiment1State
        response.body.find {e -> e.id == experiment2}.variants == expectedExperiment2State
        response.body.find {e -> e.id == experiment3}.variants == expectedExperiment3State

        when:
        startExperiment(experiment4)
        response = restTemplate.getForEntity(localUrl("/api/experiments/v3"), List)

        then:
        response.body.stream()
                .filter({e -> e.id in experiments})
                .count() == 4

        and:
        response.body.find {e -> e.id == experiment1}.variants == expectedExperiment1State
        response.body.find {e -> e.id == experiment2}.variants == expectedExperiment2State
        response.body.find {e -> e.id == experiment3}.variants == expectedExperiment3State
        response.body.find {e -> e.id == experiment4}.variants == expectedExperiment4State

        when:
        restTemplate.put(localUrl("/api/admin/experiments/${experiment2}/pause"), Map)
        response = restTemplate.getForEntity(localUrl("/api/experiments/v3"), List)

        then:
        response.body.stream()
                .filter({e -> e.id in experiments})
                .count() == 3

        and:
        response.body.find {e -> e.id == experiment1}.variants == expectedExperiment1State
        response.body.find {e -> e.id == experiment3}.variants == expectedExperiment3State
        response.body.find {e -> e.id == experiment4}.variants == expectedExperiment4State

        when:
        restTemplate.put(localUrl("/api/admin/experiments/${experiment3}/stop"), Map)
        response = restTemplate.getForEntity(localUrl("/api/experiments/v3"), List)

        then:
        response.body.stream()
                .filter({e -> e.id in experiments})
                .count() == 2

        and:
        response.body.find {e -> e.id == experiment1}.variants == expectedExperiment1State
        response.body.find {e -> e.id == experiment4}.variants == expectedExperiment4State

        when:
        restTemplate.put(localUrl("/api/admin/experiments/${experiment1}/pause"), Map)
        response = restTemplate.getForEntity(localUrl("/api/experiments/v3"), List)

        then:
        response.body.stream()
                .filter({e -> e.id in experiments})
                .count() == 1

        and:
        response.body.find {e -> e.id == experiment4}.variants == expectedExperiment4State

        when:
        restTemplate.put(localUrl("/api/admin/experiments/${experiment1}/resume"), Map)
        response = restTemplate.getForEntity(localUrl("/api/experiments/v3"), List)

        then:
        response.body.stream()
                .filter({e -> e.id in experiments})
                .count() == 2

        and:
        response.body.find {e -> e.id == experiment1}.variants == expectedExperiment1State
        response.body.find {e -> e.id == experiment4}.variants == expectedExperiment4State

        when:
        restTemplate.put(localUrl("/api/admin/experiments/${experiment2}/resume"), Map)
        response = restTemplate.getForEntity(localUrl("/api/experiments/v3"), List)

        then:
        response.body.stream()
                .filter({e -> e.id in experiments})
                .count() == 3

        and:
        response.body.find {e -> e.id == experiment1}.variants == expectedExperiment1State
        response.body.find {e -> e.id == experiment2}.variants == expectedExperiment2State
        response.body.find {e -> e.id == experiment4}.variants == expectedExperiment4State

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