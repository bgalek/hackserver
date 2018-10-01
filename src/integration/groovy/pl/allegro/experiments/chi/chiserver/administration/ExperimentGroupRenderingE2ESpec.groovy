package pl.allegro.experiments.chi.chiserver.administration


import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import spock.lang.Unroll

import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertHashRange
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRange

class ExperimentGroupRenderingE2ESpec extends BaseE2EIntegrationSpec {

    def "should not change ranges when an active experiment is added to a group "(){
        given:
        def experiment1 = startedExperiment([percentage: 5])
        def salt = experiment1.id

        when:
        def exp1 = fetchClientExperiment(experiment1.id)

        then:
        assertHashRange(exp1, 'base',    0,   5)
        assertHashRange(exp1, 'v1',     50,  55)

        when:
        createExperimentGroup([experiment1.id])
        def  freshExp = fetchClientExperiment(experiment1.id)

        then:
        assertShredRange(freshExp, 'base',  0,   5, salt)
        assertShredRange(freshExp, 'v1',   50,  55, salt)
    }

    def "should recycle percentage space"(){
        expect:
        false //TODO
    }

    @Unroll
    def "should render grouped experiments in reserved ranges when they starts and stops"() {
        given:
        def experiment1 = startedExperiment([percentage: 5])
        def experiment2 = draftExperiment([percentage: 10])
        def experiment3 = draftExperiment([percentage: 20])
        def salt = experiment1.id

        when:
        createExperimentGroup([experiment1.id, experiment2.id, experiment3.id] as List<String>)

        then:
        def exp1 = fetchClientExperiment(experiment1.id)
        def exp2 = fetchClientExperiment(experiment2.id)
        def exp3 = fetchClientExperiment(experiment3.id)

        assert exp1.status == 'ACTIVE'
        assert exp2.status == 'DRAFT'
        assert exp3.status == 'DRAFT'

        assertShredRange(exp1, 'base',  0,  5, salt)
        assertShredRange(exp1, 'v1',   50, 55, salt)

        assertShredRange(exp2, 'base', 90, 100, salt)
        assertShredRange(exp2, 'v1',    5,  15, salt)

        assertShredRange(exp3, 'base', 70,  90, salt)
        assertShredRange(exp3, 'v1',   15,  35, salt)

        when: 'third experiment is started'
        startExperiment(experiment3.id, 30)

        then:
        def exp1_v2 = fetchClientExperiment(experiment1.id)
        def exp2_v2 = fetchClientExperiment(experiment2.id)
        def exp3_v2 = fetchClientExperiment(experiment3.id)

        assert exp1_v2.status == 'ACTIVE'
        assert exp2_v2.status == 'DRAFT'
        assert exp3_v2.status == 'ACTIVE'

        assertShredRange(exp1_v2, 'base',  0,   5, salt)
        assertShredRange(exp1_v2, 'v1',   50,  55, salt)

        assertShredRange(exp2_v2, 'base', 90, 100, salt)
        assertShredRange(exp2_v2, 'v1',    5,  15, salt)

        assertShredRange(exp3_v2, 'base', 70,  90, salt)
        assertShredRange(exp3_v2, 'v1',   15,  35, salt)

        when: 'second experiment is started'
        startExperiment(experiment2.id, 30)

        then:
        def exp1_v3 = fetchClientExperiment(experiment1.id)
        def exp2_v3 = fetchClientExperiment(experiment2.id)
        def exp3_v3 = fetchClientExperiment(experiment3.id)

        exp1_v3.status == 'ACTIVE'
        exp2_v3.status == 'ACTIVE'
        exp3_v3.status == 'ACTIVE'

        assertShredRange(exp1_v3, 'base',  0,   5, salt)
        assertShredRange(exp1_v3, 'v1',   50,  55, salt)

        assertShredRange(exp2_v3, 'base', 90, 100, salt)
        assertShredRange(exp2_v3, 'v1',    5,  15, salt)

        assertShredRange(exp3_v3, 'base', 70,  90, salt)
        assertShredRange(exp3_v3, 'v1',   15,  35, salt)

        when: 'second experiment is stopped'
        stopExperiment(experiment2.id)

        then:
        def exp1_v4 = fetchClientExperiment(experiment1.id)
        def exp2_v4 = fetchClientExperiment(experiment2.id)
        def exp3_v4 = fetchClientExperiment(experiment3.id)

        exp1_v4.status == 'ACTIVE'
        exp2_v4        == null
        exp3_v4.status == 'ACTIVE'

        assertShredRange(exp1_v4, 'base',  0,   5, salt)
        assertShredRange(exp1_v4, 'v1',   50,  55, salt)

        assertShredRange(exp3_v4, 'base', 70,  90, salt)
        assertShredRange(exp3_v4, 'v1',   15,  35, salt)
    }
}
