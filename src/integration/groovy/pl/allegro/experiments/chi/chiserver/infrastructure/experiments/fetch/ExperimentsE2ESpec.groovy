package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec

import static pl.allegro.experiments.chi.chiserver.utils.SampleExperimentRequests.sampleExperimentCreationRequestProperties

class ExperimentsE2ESpec extends BaseE2EIntegrationSpec {

    def "should return all experiments as measured for admin"() {
        given:
        def draftExperiment = draftExperiment()
        def startedExperiment = startedExperiment()
        def pausedExperiment = pausedExperiment()
        def endedExperiment = endedExperiment()
        def fullOnExperiment = fullOnExperiment()

        when:
        def experiments = fetchExperiments()

        then:
        experiments.collect {it.id}.containsAll([
                draftExperiment.id,
                startedExperiment.id,
                pausedExperiment.id,
                endedExperiment.id,
                fullOnExperiment.id
        ])
    }

    def "should return BAD_REQUEST when predicate type is incorrect"() {
        given:
        def request = [
                id              : "some2",
                description     : "desc",
                variants        : [
                        [
                                name      : "v1",
                                predicates: [[type: "NOT_SUPPORTED_TYPE"]]
                        ]
                ],
                groups          : [],
                reportingEnabled: true
        ]

        when:
        createExperiment(request)

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode.value() == 400
    }

    def "should return BAD_REQUEST when no percentage"() {
        given:
        def request = sampleExperimentCreationRequestProperties([percentage: null])

        when:
        createExperiment(request)

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode.value() == 400
    }

    def "should append experiment group if experiment is in group"() {
        given:
        def experiment1 = startedExperiment()
        def experiment2 = draftExperiment()

        and:
        def groupId = createExperimentGroup([experiment1.id, experiment2.id])

        expect:
        fetchExperiment(experiment2.id as String).experimentGroup == [
                id: groupId,
                experiments: [experiment1.id, experiment2.id],
                salt: experiment1.id
        ]

        and:
        fetchExperiment(experiment2.id as String).experimentGroup == [
                id: groupId,
                experiments: [experiment1.id, experiment2.id],
                salt: experiment1.id
        ]
    }

}
