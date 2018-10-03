package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import spock.lang.Unroll

import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.*
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRange

class ClientExperimentsV3E2ESpec extends BaseE2EIntegrationSpec {

    @Unroll
    def "should ignore grouped experiments in client api version #apiVersion"() {
        given:
        def firstExperiment = draftExperiment()
        def secondExperiment = draftExperiment()
        createExperimentGroup([firstExperiment.id, secondExperiment.id])

        and:
        startExperiment(firstExperiment.id as String, 30)
        startExperiment(secondExperiment.id as String, 30)

        when:
        def experimentIds = fetchClientExperiments(apiVersion).collect { it.id }

        then:
        !experimentIds.contains(firstExperiment.id)
        !experimentIds.contains(secondExperiment.id)

        where:
        apiVersion << ['v1', 'v2']
    }

    @Unroll
    def "should return one grouped experiments in client api version #description"() {
        given:
        def firstExperiment = startedExperiment()

        when:
        createExperimentGroup([firstExperiment.id])
        def experiments = fetchClientExperiments(apiVersion)

        then:
        def exp1 = experiments.find { it.id == firstExperiment.id }

        exp1.variants.size() == 2
        exp1.status == 'ACTIVE'
        assertShredRange(exp1, 'base', 90, 100, firstExperiment.id)
        assertShredRange(exp1, 'v1', 0, 10, firstExperiment.id)

        where:
        description | apiVersion
        'v3'        | 'v3'
        'latest'    | ''
    }

    @Unroll
    def "should render two grouped experiments in client API version #description"() {
        given:
        def firstExperiment = startedExperiment()
        def secondExperiment = draftExperiment()

        when:
        createExperimentGroup([firstExperiment.id, secondExperiment.id])
        def experiments = fetchClientExperiments(apiVersion)

        then:
        def exp1 = experiments.find { it.id == firstExperiment.id }

        assert exp1.variants.size() == 2
        assert exp1.status == 'ACTIVE'

        assertShredRange(exp1, 'base', 90, 100, firstExperiment.id)
        assertShredRange(exp1, 'v1', 0, 10, firstExperiment.id)

        and:
        def exp2 = experiments.find { it.id == secondExperiment.id }

        assert exp2.variants.size() == 2
        assert exp2.status == 'DRAFT'

        assertShredRange(exp2, 'base', 80, 90, firstExperiment.id)
        assertShredRange(exp2, 'v1', 10, 20, firstExperiment.id)

        where:
        description | apiVersion
        'v3'        | 'v3'
        'latest'    | ''
    }

    @Unroll
    def "should render grouped experiments in client API version #description"() {
        given:
        def experiment1 = startedExperiment([percentage: 5])
        def salt = experiment1.id

        when:
        createExperimentGroup([experiment1.id])
        def experiments = fetchClientExperiments(apiVersion)

        then:
        def exp1 = experiments.find { it.id == experiment1.id }

        exp1.status == 'ACTIVE'

        assertShredRange(exp1, 'base',   0,  5, salt)
        assertShredRange(exp1, 'v1',    50, 55, salt)

        where:
        description | apiVersion
        'v3'        | 'v3'
        'latest'    | ''
    }

    @Unroll
    def "should ignore #status experiments when rendering grouped experiments"() {
        given:
        def firstExperiment = experimentWithStatus(status)
        def secondExperiment = draftExperiment()
        createExperimentGroup([firstExperiment.id, secondExperiment.id])

        when:
        def experimentIds = fetchClientExperiments('v3').collect { it.id }

        then:
        !experimentIds.contains(firstExperiment.id)

        where:
        status << [PAUSED]
    }
}