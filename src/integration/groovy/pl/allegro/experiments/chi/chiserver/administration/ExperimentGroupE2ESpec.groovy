package pl.allegro.experiments.chi.chiserver.administration

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus
import spock.lang.Unroll

import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.DRAFT
import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.FULL_ON

class ExperimentGroupE2ESpec extends BaseE2EIntegrationSpec {

    def "should delete DRAFT experiment bound to group"() {
        given:
        def experiment1 = draftExperiment()
        def experiment2 = draftExperiment()
        def group = createExperimentGroupAndFetch([experiment1.id, experiment2.id])

        when:
        deleteExperiment(experiment1.id as String)

        then:
        fetchExperimentGroup(group.id as String).experiments == [experiment2.id]

        when:
        fetchExperiment(experiment1.id as String)

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.NOT_FOUND
    }

    @Unroll
    def "should not delete #status experiment bound to group"() {
        given:
        def experiment = experimentWithStatus(status)
        createExperimentGroup([experiment.id, draftExperiment().id])

        when:
        deleteExperiment(experiment.id as String)

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.BAD_REQUEST

        where:
        status << allExperimentStatusValuesExcept(DRAFT, FULL_ON)
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
