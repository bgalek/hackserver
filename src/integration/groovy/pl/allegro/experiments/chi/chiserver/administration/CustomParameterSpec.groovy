package pl.allegro.experiments.chi.chiserver.administration

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import spock.lang.Unroll

class CustomParameterSpec extends BaseE2EIntegrationSpec {

    def "should save experiment with custom parameter definition and return it for clients"() {
        given:
        def experiment = draftExperiment([
                customParameterName : 'this is a name',
                customParameterValue: 'this is a value'
        ])

        when:
        experiment = fetchExperiments().find { it.id == experiment.id } as Map

        then:
        experiment.status == "DRAFT"
        experiment.renderedVariants.find { it.name == 'v1' }
                .predicates.contains([type: 'CUSTOM_PARAM', name: 'this is a name', value: 'this is a value'])
    }

    @Unroll
    def "should not save experiment with partially defined custom parameter"() {
        when:
        draftExperiment([
                customParameterName : name,
                customParameterValue: 'value'
        ])

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.BAD_REQUEST

        where:
        name << [null, '', ' ']
    }

    @Unroll
    def "should ignore blank custom parameter fields"() {
        given:
        def experiment = draftExperiment([
                customParameterName : name,
                customParameterValue: null
        ])

        when:
        experiment = fetchExperiments().find { it.id == experiment.id }

        then:
        !experiment.renderedVariants.find { it.name == 'v1' }
                .predicates.any {it.type == 'CUSTOM_PARAM'}

        where:
        name << [null, '', ' ']
    }

    def "should trim not blank fields"() {
        given:
        def experiment = draftExperiment([
                customParameterName : ' notBlank ',
                customParameterValue: ' this will be trimmed '
        ])

        when:
        experiment = fetchExperiments().find { it.id == experiment.id } as Map

        then:
        experiment.renderedVariants.find({ it.name == 'v1' })
                .predicates.contains([type: 'CUSTOM_PARAM', name: 'notBlank', value: 'this will be trimmed'])
    }
}
