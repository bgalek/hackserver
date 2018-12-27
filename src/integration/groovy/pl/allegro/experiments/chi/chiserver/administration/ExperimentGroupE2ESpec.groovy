package pl.allegro.experiments.chi.chiserver.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.AllocationTable
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.ClientExperimentFactory
import spock.lang.Unroll

import java.time.ZonedDateTime

import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinitionBuilder.experimentDefinition
import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.*
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertHasSpecifiedInternalVariantWithDeviceClass
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRangeWithDeviceClass

class ExperimentGroupE2ESpec extends BaseE2EIntegrationSpec {
    @Autowired
    ExperimentGroupRepository experimentGroupRepository

    @Autowired
    ExperimentsRepository experimentsRepository

    @Autowired
    ClientExperimentFactory clientExperimentFactory

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

    def "should remove experiment from a group after it is deleted"() {
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

    def "should removed ENDED experiment from a group and free allocated space"(){
        given:
        def experiment1 = experimentWithStatus(ENDED)
        def experiment2 = draftExperiment()
        def group = new ExperimentGroup(UUID.randomUUID().toString(), "salt",
                [experiment1.id, experiment2.id],
                new AllocationTable([]).allocate (experiment1.id, ['v1', 'base'], 10))

        experimentGroupRepository.save(group)
        with(fetchExperimentGroup(group.id)) {
            assert experiments.size() == 2
            assert allocationTable.size() == 2
        }

        when:
        removeFromGroup(experiment1.id)

        then:
        def newGroup = fetchExperimentGroup(group.id)
        newGroup.experiments == [experiment2.id]
        newGroup.allocationTable == []

        fetchExperiment(experiment1.id).status == 'ENDED'
    }

    def "should remove DRAFT experiments from a group and free allocated space"(){
        given:
        def experiment1 = draftExperiment()
        def experiment2 = draftExperiment()
        def group = createExperimentGroupAndFetch([experiment1.id, experiment2.id])

        with(fetchExperimentGroup(group.id)) {
            assert experiments.size() == 2
            assert allocationTable.size() == 4
        }

        when:
        removeFromGroup(experiment1.id)
        def newGroup = fetchExperimentGroup(group.id)

        then:
        newGroup.experiments == [experiment2.id]
        newGroup.allocationTable.size() == 2

        when:
        removeFromGroup(experiment2.id)
        newGroup = fetchExperimentGroup(group.id)

        then:
        newGroup.experiments == []
        newGroup.allocationTable == []
    }

    @Unroll
    def "should not allow to remove #status experiment from a group"(){
        given:
        def experiment1 = experimentWithStatus(status)
        def experiment2 = draftExperiment()
        createExperimentGroupAndFetch([experiment1.id, experiment2.id])

        when:
        removeFromGroup(experiment1.id)

        then:
        thrown(HttpClientErrorException.BadRequest)

        where:
        status << [ACTIVE, PAUSED]
    }
}
