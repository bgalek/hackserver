package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.application.administration.RenderedExperimentVariantsBuilder
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.CachedExperimentGroupRepository
import spock.lang.Unroll

import static pl.allegro.experiments.chi.chiserver.application.administration.RenderedExperimentVariantsBuilder.Range.rangeOf

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
        response.numberOfExperimentsPresent(experiments) == 0

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
        response.numberOfExperimentsPresent(experiments) == 2

        and:
        response.experimentVariants(experimentId1) == RenderedExperimentVariantsBuilder.builder()
                .addVariant('base', experimentId1, [rangeOf(90, 100)])
                .addVariant('v1', experimentId1, [rangeOf(0, 10)])
                .build()

        and:
        response.experimentVariants(experimentId2) == RenderedExperimentVariantsBuilder.builder()
                .addVariant('base', experimentId1, [rangeOf(90, 100)])
                .addVariant('v2', experimentId1, [rangeOf(10, 20)])
                .build()
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
        response.numberOfExperimentsPresent(experiments) == 0
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
        response.numberOfExperimentsPresent(experiments) == 2

        and:
        response.experimentVariants(experimentId1) == RenderedExperimentVariantsBuilder.builder()
                .addVariant('base', experimentId1, [rangeOf(90, 100)])
                .addVariant('v1', experimentId1, [rangeOf(0, 10)])
                .build()
    }

    def "should render grouped experiments ranges in deterministic manner"() {
        given:
        def (String experiment1, String experiment2, String experiment3, String experiment4) = (1..4).collect { i ->
            UUID.randomUUID().toString()
        }

        and:
        def expectedExperiment1State = RenderedExperimentVariantsBuilder.builder()
                .addVariant('base', experiment1, [rangeOf(95, 100)])
                .addVariant('v1', experiment1, [rangeOf(0, 5)])
                .build()

        def expectedExperiment2State = RenderedExperimentVariantsBuilder.builder()
                .addVariant('base', experiment1, [rangeOf(90, 100)])
                .addVariant('v1', experiment1, [rangeOf(5, 15)])
                .build()

        def expectedExperiment3State = RenderedExperimentVariantsBuilder.builder()
                .addVariant('base', experiment1, [rangeOf(80, 100)])
                .addVariant('v1', experiment1, [rangeOf(15, 35)])
                .build()

        def expectedExperiment4State = RenderedExperimentVariantsBuilder.builder()
                .addVariant('base', experiment1, [rangeOf(85, 100)])
                .addVariant('v1', experiment1, [rangeOf(35, 50)])
                .build()

        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        and:
        def experiments = [experiment2, experiment3, experiment4, experiment1]
        createDraftExperiment(experiment1, ['base', 'v1'], 5)
        createDraftExperiment(experiment2, ['base', 'v1'], 10)
        createDraftExperiment(experiment3, ['base', 'v1'], 20)
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