package pl.allegro.experiments.chi.chiserver.application.commands

import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinition
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException
import spock.lang.Shared
import spock.lang.Unroll

import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.DRAFT
import static pl.allegro.experiments.chi.chiserver.domain.experiments.ReportingType.*

class UpdateEventDefinitionsCommandIntegrationSpec extends BaseCommandIntegrationSpec {

    @Shared EventDefinition firstEventDefinition =
            new EventDefinition('c1', 'a1', 'v1', 'l1', 'b1')
    @Shared EventDefinition secondEventDefinition =
            new EventDefinition('c2', 'a2', 'v2', 'l2', 'b2')
    @Shared EventDefinition thirdEventDefinition =
            new EventDefinition('c3', 'a3', 'v3', 'l3', 'b3')


    @Unroll
    def "should update event definitions if experiment is #status"() {
        given:
        def experiment = draftExperiment([
                reportingType   : FRONTEND,
                eventDefinitions: [firstEventDefinition, secondEventDefinition]
        ])

        when:
        updateExperimentEventDefinitions(experiment.id, [firstEventDefinition, thirdEventDefinition])

        then:
        def reportingDefinition = fetchExperimentDefinition(experiment.id).reportingDefinition
        reportingDefinition.type == FRONTEND
        reportingDefinition.eventDefinitions == [firstEventDefinition, thirdEventDefinition]

        where:
        status << [DRAFT]
    }

    @Unroll
    def "should not update event definitions if experiment is #status"() {
        given:
        def experiment = endedExperiment([
                reportingType   : FRONTEND,
                eventDefinitions: [firstEventDefinition, secondEventDefinition]
        ])

        when:
        updateExperimentEventDefinitions(experiment.id, [firstEventDefinition, thirdEventDefinition])

        then:
        def exception = thrown ExperimentCommandException
        exception.message == "ENDED experiment event definitions cant be updated"

        where:
        status << allExperimentStatusValuesExcept(DRAFT)
    }

    @Unroll
    def "should not update event definitions if experiment reporting type is #reportingType"() {
        given:
        def experiment = draftExperiment([
                reportingType   : reportingType,
                eventDefinitions: [firstEventDefinition, secondEventDefinition]
        ])

        when:
        updateExperimentEventDefinitions(experiment.id, [firstEventDefinition, thirdEventDefinition])

        then:
        def exception = thrown ExperimentCommandException
        exception.message == "Non frontend experiment has no event definitions"

        where:
        reportingType << [GTM, BACKEND]
    }
}
