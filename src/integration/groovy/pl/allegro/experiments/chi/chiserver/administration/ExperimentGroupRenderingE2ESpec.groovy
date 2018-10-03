package pl.allegro.experiments.chi.chiserver.administration

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.PercentageRange
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

    def "should allow to scale-up a grouped experiment"() {
        given:
        def experiment1 = startedExperiment()
        def experiment2 = draftExperiment()

        def group = createExperimentGroupAndFetch([experiment1.id, experiment2.id])
        def salt = group.salt

        when:
        def exp1_v1 = fetchClientExperiment(experiment1.id)

        then:
        assertShredRange(exp1_v1,   'v1', [new PercentageRange(50, 60)], salt)
        assertShredRange(exp1_v1, 'base', [new PercentageRange(0, 10)],  salt)

        when:
        updateExperimentVariants(experiment1.id, null, 20, 'all')
        def exp1_v2 = fetchClientExperiment(experiment1.id)

        then:
        assertShredRange(exp1_v2,   'v1', [new PercentageRange(20, 30), new PercentageRange(50, 60)], salt)
        assertShredRange(exp1_v2, 'base', [new PercentageRange(80, 90), new PercentageRange(0, 10)],  salt)
    }

    def "should not free allocated space when experiment is scaled-down"(){
        given:
        def experiment1 = startedExperiment()
        def experiment2 = draftExperiment()

        def group = createExperimentGroupAndFetch([experiment1.id, experiment2.id])

        when:
        updateExperimentVariants(experiment1.id, null, 5, 'all')
        def exp1_v2 = fetchClientExperiment(experiment1.id)
        def group_v2 = fetchExperimentGroup(group.id)

        then:
        assertShredRange(exp1_v2,   'v1', [new PercentageRange(50, 55)], group.salt)
        assertShredRange(exp1_v2, 'base', [new PercentageRange(5,  10)],  group.salt)

        group_v2.allocationTable.size() == 4
        group_v2.allocationTable[0].experimentId == experiment1.id
        group_v2.allocationTable[0].variant == "base"
        group_v2.allocationTable[0].range == [from:0,  to:10]
        group_v2.allocationTable[2].experimentId == experiment1.id
        group_v2.allocationTable[2].variant == "v1"
        group_v2.allocationTable[2].range == [from:50, to:60]
    }

    def "should not allow to scale grouped experiment when there is not enough space"() {
        given:
        def experiment1 = startedExperiment()
        def experiment2 = draftExperiment()

        def group = createExperimentGroupAndFetch([experiment1.id, experiment2.id])

        when:
        updateExperimentVariants(experiment1.id, null, 50, 'all')

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.BAD_REQUEST
        println exception
    }
}
