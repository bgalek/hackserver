package pl.allegro.experiments.chi.chiserver.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.AllocationTable
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.ClientExperimentFactory
import spock.lang.Unroll
import java.time.ZonedDateTime
import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinitionBuilder.experimentDefinition
import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.*
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRange

class ExperimentGroupE2ESpec extends BaseE2EIntegrationSpec {
    @Autowired
    ExperimentGroupRepository experimentGroupRepository

    @Autowired
    ExperimentsRepository experimentsRepository

    @Autowired
    ClientExperimentFactory clientExperimentFactory

    /**
     * remove after migration
     */
    @Deprecated
    def "should migrate legacy group"(){
        given:
        def salt = 'salt'
        def experiment1 = experimentDefinition().id(UUID.randomUUID().toString())
                                                .variantNames(["base","enabled","simplified"])
                                                .percentage(10).activityPeriod(ZonedDateTime.now(), ZonedDateTime.now().plusDays(1)).build()

        def experiment2 = experimentDefinition().id(UUID.randomUUID().toString())
                                                .variantNames(["base","enabled","simplified"])
                                                .percentage(5).activityPeriod(ZonedDateTime.now(), ZonedDateTime.now().plusDays(1)).build()

        experimentsRepository.save(experiment1)
        experimentsRepository.save(experiment2)

        def group = new ExperimentGroup(UUID.randomUUID().toString(), salt, [experiment1.id, experiment2.id], AllocationTable.empty())
        experimentGroupRepository.save(group)

        when:
        clientExperimentFactory.persistAllocationForLegacyGroup(group)

        then:
        def fresh = experimentGroupRepository.findById(group.id).get()
        fresh.experiments == [experiment1.id, experiment2.id]
        fresh.salt == salt
        fresh.allocationTable.records.size() == 5

        def exp1_v2 = fetchClientExperiment(experiment1.id)
        def exp2_v2 = fetchClientExperiment(experiment2.id)

        assertShredRange(exp1_v2, 'base',          90, 100, salt)
        assertShredRange(exp1_v2, 'enabled',        0,  10, salt)
        assertShredRange(exp1_v2, 'simplified',    10,  20, salt)
        assertShredRange(exp2_v2, 'base',          95, 100, salt)
        assertShredRange(exp2_v2, 'enabled',       20,  25, salt)
        assertShredRange(exp2_v2, 'simplified',    25,  30, salt)
    }

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
