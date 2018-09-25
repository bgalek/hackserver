package pl.allegro.experiments.chi.chiserver.administration

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus
import spock.lang.Unroll
import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.*
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRange
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRange
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRange
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRange
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRange
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRange
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRange
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRange
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRange
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRange
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRange
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRange
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRange
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRange
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRange
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRange
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRange
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRange

class ExperimentGroupE2ESpec extends BaseE2EIntegrationSpec {

    @Unroll
    def "should delete #status experiment bounded to a group and free allocated space"() {
        given:
        def experiment1 = experimentWithStatus(status)
        def experiment2 = draftExperiment()
        def group = createExperimentGroupAndFetch([experiment1.id, experiment2.id])

        when:
        deleteExperiment(experiment1.id as String)

        then:
        fetchExperimentGroup(group.id as String).experiments == [experiment2.id]
        //TODO "should free allocated space "

        when:
        fetchExperiment(experiment1.id as String)

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.NOT_FOUND

        where:
        status << [ACTIVE, DRAFT, PAUSED, ENDED]
    }

    def "should free allocated space when an experiment ends"(){
        expect:
        false //TODO
    }

    def "should free allocated space when an experiment is deleted"(){
        expect:
        false //TODO
    }

    def "should allow to scale grouped experiment when there is enough space"(){
        expect:
        false //TODO FRONT!!
    }

    def "should not allow to scale grouped experiment when there is not enough space"(){
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
