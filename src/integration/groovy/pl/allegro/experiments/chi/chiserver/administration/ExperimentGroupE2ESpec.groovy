package pl.allegro.experiments.chi.chiserver.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.AllocationTable
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository
import spock.lang.Unroll

import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.*

class ExperimentGroupE2ESpec extends BaseE2EIntegrationSpec {
    @Autowired
    ExperimentGroupRepository experimentGroupRepository

    //TODO fronted?

    @Unroll
    def "should delete #status experiment bounded to a group and free allocated space"() {
        given:
        def experiment1 = experimentWithStatus(status)
        def experiment2 = draftExperiment()
        def group = createExperimentGroupAndFetch([experiment1.id, experiment2.id])

        assert fetchExperimentGroup(group.id).allocationTable.size() == 4

        when:
        deleteExperiment(experiment1.id as String)

        then:
        def fetchedGroup = fetchExperimentGroup(group.id)
        fetchedGroup.experiments == [experiment2.id]
        fetchedGroup.allocationTable.size() == 2
        fetchedGroup.allocationTable[0].experimentId == experiment2.id
        fetchedGroup.allocationTable[1].experimentId == experiment2.id

        when:
        fetchExperiment(experiment1.id as String)

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.NOT_FOUND

        where:
        status << [ACTIVE, DRAFT, PAUSED]
    }

    def "should delete ENDED experiment bounded to a group and free allocated space "() {
        given:
        def exp = experimentWithStatus(ENDED)
        def group = new ExperimentGroup(UUID.randomUUID().toString(), "salt", [exp.id],
                new AllocationTable([]).allocate (exp.id, ['v1', 'base'], 10))

        experimentGroupRepository.save(group)
        assert fetchExperimentGroup(group.id).allocationTable.size() == 2

        when:
        deleteExperiment(exp.id as String)

        then:
        assert fetchExperimentGroup(group.id).allocationTable.size() == 0
    }

    def "should free allocated space when an experiment ends"() {
        expect:
        false //TODO
    }

    def "should allow to scale grouped experiment when there is enough space"() {
        expect:
        false //TODO FRONT!!
    }

    def "should not allow to scale grouped experiment when there is not enough space"() {
        expect:
        false //TODO
    }

    def "should set experiment as list tail when starting"() {
        given:
        def experiment1 = draftExperiment()
        def experiment2 = draftExperiment()

        def group = createExperimentGroupAndFetch([experiment1.id, experiment2.id])
        startExperiment(experiment2.id as String, 30)

        when:
        startExperiment(experiment1.id as String, 30)

        then:
        fetchExperimentGroup(group.id as String).experiments == [experiment2.id, experiment1.id]
    }
}
