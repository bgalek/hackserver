package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import spock.lang.Unroll

class ClientExperimentsV4E2ESpec extends BaseE2EIntegrationSpec {

    @Unroll
    def "should ignore experiment with customParam in client API #apiVersion"() {
        given:
        def experiment = draftExperiment([
                customParameterName: "name",
                customParameterValue   : "value"
        ])

        when:
        def experiments = fetchClientExperiments(apiVersion)

        then:
        !experiments.collect {it.id}.contains(experiment.id)

        where:
        apiVersion << ["v1", "v2", "v3"]
    }

    @Unroll
    def "should serve experiment with customParam in client API #description"() {
        given:
        def experiment = draftExperiment([
                customParameterName: "name",
                customParameterValue   : "value"
        ])

        when:
        def experiments = fetchClientExperiments(apiVersion)

        then:
        experiments.collect {it.id}.contains(experiment.id)

        where:
        description | apiVersion
        'v4'        | 'v4'
        'latest'    | ''
    }
}