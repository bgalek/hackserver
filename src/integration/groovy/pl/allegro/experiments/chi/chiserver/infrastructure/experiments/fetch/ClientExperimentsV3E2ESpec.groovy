package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import spock.lang.Unroll

import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.*

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

    def "should return one grouped experiments in client api version #description"() {
        given:
        def firstExperiment = startedExperiment()

        when:
        createExperimentGroup([firstExperiment.id])
        def experiments = fetchClientExperiments(apiVersion)

        then:
        def exp1 = experiments.find { it.id == firstExperiment.id }

        assert exp1.variants.size() == 2
        assert exp1.status == 'ACTIVE'
        assertShredRange(exp1, 'base', 90, 100, firstExperiment.id)
        assertShredRange(exp1, 'v1', 0, 10, firstExperiment.id)

        where:
        description | apiVersion
        'v3'        | 'v3'
        'latest'    | ''
    }

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

        assertShredRange(exp2, 'base', 90, 100, firstExperiment.id)
        assertShredRange(exp2, 'v1', 10, 20, firstExperiment.id)

        where:
        description | apiVersion
        'v3'        | 'v3'
        'latest'    | ''
    }

    @Unroll
    def "should render three grouped experiments ranges in deterministic manner in client API version #description"() {
        given:
        def experiment1 = startedExperiment([percentage: 5])
        def experiment2 = draftExperiment([percentage: 10])
        def experiment3 = draftExperiment([percentage: 20])
        def salt = experiment1.id

        when:
        createExperimentGroup([experiment1.id, experiment2.id, experiment3.id] as List<String>)
        def experiments = fetchClientExperiments(apiVersion)

        then:
        def exp1 = experiments.find { it.id == experiment1.id }
        def exp2 = experiments.find { it.id == experiment2.id }
        def exp3 = experiments.find { it.id == experiment3.id }

        assert exp1.status == 'ACTIVE'
        assert exp2.status == 'DRAFT'
        assert exp3.status == 'DRAFT'

        assertShredRange(exp1, 'base', 95, 100, salt)
        assertShredRange(exp1, 'v1',    0,   5, salt)

        assertShredRange(exp2, 'base', 90, 100, salt)
        assertShredRange(exp2, 'v1',    5,  15, salt)

        assertShredRange(exp3, 'base', 80, 100, salt)
        assertShredRange(exp3, 'v1',    5,  25, salt)

        when: 'third experiment is started'
        startExperiment(experiment3.id as String, 30)
        def experiments_ = fetchClientExperiments(apiVersion)

        then:
        def exp1_ = experiments_.find { it.id == experiment1.id }
        def exp2_ = experiments_.find { it.id == experiment2.id }
        def exp3_ = experiments_.find { it.id == experiment3.id }

        assert exp1_.status == 'ACTIVE'
        assert exp2_.status == 'DRAFT'
        assert exp3_.status == 'ACTIVE'

        assertShredRange(exp1_, 'base', 95, 100, salt)
        assertShredRange(exp1_, 'v1',    0,   5, salt)

        assertShredRange(exp2_, 'base', 90, 100, salt)
        assertShredRange(exp2_, 'v1',    5,  15, salt)

        assertShredRange(exp3_, 'base', 80, 100, salt)
        assertShredRange(exp3_, 'v1',    5,  25, salt)

        when: 'third second is started'
        startExperiment(experiment2.id as String, 30)
        def experiments__ = fetchClientExperiments(apiVersion)

        then:
        def exp1__ = experiments__.find { it.id == experiment1.id }
        def exp2__ = experiments__.find { it.id == experiment2.id }
        def exp3__ = experiments__.find { it.id == experiment3.id }

        assert exp1__.status == 'ACTIVE'
        assert exp2__.status == 'ACTIVE'
        assert exp3__.status == 'ACTIVE'

        assertShredRange(exp1__, 'base', 95, 100, salt)
        assertShredRange(exp1__, 'v1',    0,   5, salt)

        assertShredRange(exp2__, 'base', 90, 100, salt)
        assertShredRange(exp2__, 'v1',   25,  35, salt)

        assertShredRange(exp3__, 'base', 80, 100, salt)
        assertShredRange(exp3__, 'v1',    5,  25, salt)

        where:
        description | apiVersion
        'v3'        | 'v3'
        'latest'    | ''
    }


    void assertShredRange(Map experiment, String variantName, int from, int to, String salt) {
        def variant = experiment.variants.find {it.name == variantName}
        assert variant == [
                name      : variantName,
                predicates: [
                        [
                                type  : 'SHRED_HASH',
                                ranges: [[from: from, to: to]],
                                salt  : salt
                        ]
                ]
        ]
    }

    @Unroll
    def "should ignore #status experiments when rendering grouped experiments"() {
        given:
        def firstExperiment = experimentWithStatus(status)
        def secondExperiment = draftExperiment()
        createExperimentGroup([firstExperiment.id, secondExperiment.id])

        when:
        def experimentIds = fetchClientExperiments().collect { it.id }

        then:
        !experimentIds.contains(firstExperiment.id)

        where:
        status << [PAUSED]
    }
}