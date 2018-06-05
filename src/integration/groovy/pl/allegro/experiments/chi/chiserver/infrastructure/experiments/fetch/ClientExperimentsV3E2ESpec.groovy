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

class ClientExperimentsV3E2ESpec extends BaseIntegrationSpec {

    @Autowired
    ExperimentGroupRepository experimentGroupRepository

    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    UserProvider userProvider

    def setup() {
        if (!experimentGroupRepository instanceof CachedExperimentGroupRepository) {
            throw new RuntimeException("We should test cached repository")
        }
        ResponseEntity.metaClass.experimentVariants << { String experimentId ->
            getBody().find {e -> e.id == experimentId}.variants
        }
        ResponseEntity.metaClass.numberOfExperimentsPresent << { List<String> experimentIds ->
            getBody().stream()
                    .filter({e -> e.id in experimentIds})
                    .count()
        }
    }

    @Unroll
    def "should ignore grouped experiments in client api version #apiVersion"() {
        given:
        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        and:
        String experimentId1 = createDraftExperiment(['base', 'v1'])
        String experimentId2 = createDraftExperiment(['base', 'v1'])
        def experiments = [experimentId1, experimentId2]

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
        response.numberOfExperimentsPresent(experiments) == 0

        where:
        apiVersion << ['v1', 'v2']
    }

    def "should return grouped experiments in v3"() {
        given:
        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        and:
        String experimentId1 = createDraftExperiment(['base', 'v1'])
        String experimentId2 = createDraftExperiment(['base', 'v2'])
        def experiments = [experimentId1, experimentId2]
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
        response.numberOfExperimentsPresent(experiments) == 2

        and:
        response.experimentVariants(experimentId1) == [
                [
                        name      : 'base',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 90, to: 100]],
                                        salt  : experimentId1
                                ]
                        ]
                ],
                [
                        name      : 'v1',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 0, to: 10]],
                                        salt  : experimentId1
                                ]
                        ]
                ]
        ]

        and:
        response.experimentVariants(experimentId2) == [
                [
                        name      : 'base',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 90, to: 100]],
                                        salt  : experimentId1
                                ]
                        ]
                ],
                [
                        name      : 'v2',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 10, to: 20]],
                                        salt  : experimentId1
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
        String experimentId1 = createDraftExperiment(['base', 'v1'])
        String experimentId2 = createDraftExperiment(['base', 'v2'])
        def experiments = [experimentId1, experimentId2]
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
        response.numberOfExperimentsPresent(experiments) == 0
    }

    def "should not ignore DRAFT experiments when rendering grouped experiments"() {
        given:
        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        and:
        String experimentId1 = createDraftExperiment(['base', 'v1'])
        String experimentId2 = createDraftExperiment(['base', 'v2'])
        def experiments = [experimentId1, experimentId2]
        startExperiment(experimentId1)

        and:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId,
                experiments: [experimentId1, experimentId2]
        ], Map)

        when:
        def response = restTemplate.getForEntity(localUrl("/api/experiments/v3"), List)

        then:
        response.numberOfExperimentsPresent(experiments) == 2

        and:
        response.experimentVariants(experimentId1) == [
                [
                        name      : 'base',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 90, to: 100]],
                                        salt  : experimentId1
                                ]
                        ]
                ],
                [
                        name      : 'v1',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 0, to: 10]],
                                        salt  : experimentId1
                                ]
                        ]
                ]
        ]
    }

    def "should render grouped experiments ranges in deterministic manner"() {
        given:
        String experiment1 = createDraftExperiment(['base', 'v1'], 5)
        String experiment2 = createDraftExperiment(['base', 'v1'], 10)
        String experiment3 = createDraftExperiment(['base', 'v1'], 20)
        String experiment4 = createDraftExperiment(['base', 'v1'], 15)
        def experiments = [experiment2, experiment3, experiment4, experiment1]

        and:
        def expectedExperiment1State = [
                [
                        name      : 'base',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 95, to: 100]],
                                        salt  : experiment1
                                ]
                        ]
                ],
                [
                        name      : 'v1',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 0, to: 5]],
                                        salt  : experiment1
                                ]
                        ]
                ]
        ]

        def expectedExperiment2State = [
                [
                        name      : 'base',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 90, to: 100]],
                                        salt  : experiment1
                                ]
                        ]
                ],
                [
                        name      : 'v1',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 5, to: 15]],
                                        salt  : experiment1
                                ]
                        ]
                ]
        ]

        def expectedExperiment3State = [
                [
                        name      : 'base',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 80, to: 100]],
                                        salt  : experiment1
                                ]
                        ]
                ],
                [
                        name      : 'v1',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 15, to: 35]],
                                        salt  : experiment1
                                ]
                        ]
                ]
        ]

        def expectedExperiment4State = [
                [
                        name      : 'base',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 85, to: 100]],
                                        salt  : experiment1
                                ]
                        ]
                ],
                [
                        name      : 'v1',
                        predicates: [
                                [
                                        type  : 'SHRED_HASH',
                                        ranges: [[from: 35, to: 50]],
                                        salt  : experiment1
                                ]
                        ]
                ]
        ]

        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        startExperiment(experiment1)

        and:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId,
                experiments: experiments
        ], Map)

        when:
        def response = restTemplate.getForEntity(localUrl("/api/experiments/v3"), List)

        then:
        response.numberOfExperimentsPresent(experiments) == 4

        and:
        response.experimentVariants(experiment1) == expectedExperiment1State

        when:
        startExperiment(experiment2)
        response = restTemplate.getForEntity(localUrl("/api/experiments/v3"), List)

        then:
        response.numberOfExperimentsPresent(experiments) == 4

        and:
        response.experimentVariants(experiment1) == expectedExperiment1State
        response.experimentVariants(experiment2) == expectedExperiment2State

        when:
        startExperiment(experiment3)
        response = restTemplate.getForEntity(localUrl("/api/experiments/v3"), List)

        then:
        response.numberOfExperimentsPresent(experiments) == 4

        and:
        response.experimentVariants(experiment1) == expectedExperiment1State
        response.experimentVariants(experiment2) == expectedExperiment2State
        response.experimentVariants(experiment3) == expectedExperiment3State

        when:
        startExperiment(experiment4)
        response = restTemplate.getForEntity(localUrl("/api/experiments/v3"), List)

        then:
        response.numberOfExperimentsPresent(experiments) == 4

        and:
        response.experimentVariants(experiment1) == expectedExperiment1State
        response.experimentVariants(experiment2) == expectedExperiment2State
        response.experimentVariants(experiment3) == expectedExperiment3State
        response.experimentVariants(experiment4) == expectedExperiment4State

        when:
        restTemplate.put(localUrl("/api/admin/experiments/${experiment2}/pause"), Map)
        response = restTemplate.getForEntity(localUrl("/api/experiments/v3"), List)

        then:
        response.numberOfExperimentsPresent(experiments) == 3

        and:
        response.experimentVariants(experiment1) == expectedExperiment1State
        response.experimentVariants(experiment3) == expectedExperiment3State
        response.experimentVariants(experiment4) == expectedExperiment4State

        when:
        restTemplate.put(localUrl("/api/admin/experiments/${experiment3}/stop"), Map)
        response = restTemplate.getForEntity(localUrl("/api/experiments/v3"), List)

        then:
        response.numberOfExperimentsPresent(experiments) == 2

        and:
        response.experimentVariants(experiment1) == expectedExperiment1State
        response.experimentVariants(experiment4) == expectedExperiment4State

        when:
        restTemplate.put(localUrl("/api/admin/experiments/${experiment1}/pause"), Map)
        response = restTemplate.getForEntity(localUrl("/api/experiments/v3"), List)

        then:
        response.numberOfExperimentsPresent(experiments) == 1

        and:
        response.experimentVariants(experiment4) == expectedExperiment4State

        when:
        restTemplate.put(localUrl("/api/admin/experiments/${experiment1}/resume"), Map)
        response = restTemplate.getForEntity(localUrl("/api/experiments/v3"), List)

        then:
        response.numberOfExperimentsPresent(experiments) == 2

        and:
        response.experimentVariants(experiment1) == expectedExperiment1State
        response.experimentVariants(experiment4) == expectedExperiment4State

        when:
        restTemplate.put(localUrl("/api/admin/experiments/${experiment2}/resume"), Map)
        response = restTemplate.getForEntity(localUrl("/api/experiments/v3"), List)

        then:
        response.numberOfExperimentsPresent(experiments) == 3

        and:
        response.experimentVariants(experiment1) == expectedExperiment1State
        response.experimentVariants(experiment2) == expectedExperiment2State
        response.experimentVariants(experiment4) == expectedExperiment4State
    }

    def createDraftExperiment(List<String> variants, int percentage=10) {
        String experimentId = UUID.randomUUID().toString()
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
        experimentId
    }
}