package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import spock.lang.Unroll

class ClientExperimentsV5E2ESpec extends BaseE2EIntegrationSpec {

    @Unroll
    def "should ignore full-on experiments in client API #apiVersion"() {
        given:
        def draftExperiment = draftExperiment()
        def startedExperiment = startedExperiment()
        def fullOnExperiment = fullOnExperiment()

        when:
        def experiments = fetchClientExperiments(apiVersion)

        then:
        def experimentIds = experiments.collect { it.id }
        experimentIds.containsAll([draftExperiment.id, startedExperiment.id])
        !experimentIds.contains(fullOnExperiment.id)

        where:
        apiVersion << ['v1', 'v2', 'v3', 'v4']
    }

    @Unroll
    def "should return full-on experiment in client API #apiDesc"() {
        given:
        def experiment = fullOnExperiment()

        when:
        def experiments = fetchClientExperiments(apiVersion)

        then:
        experiments.contains([
            id: experiment.id,
            status: "FULL_ON",
            reportingEnabled: true,
            variants: [
                [
                    name: "v1",
                    predicates: [
                        [
                            type: "FULL_ON"
                        ]
                    ]
                ]
            ]
        ])

        where:
        description | apiVersion
        'v5'        | 'v5'
        'latest'    | ''
    }
}