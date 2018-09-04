package pl.allegro.experiments.chi.chiserver.administration

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import spock.lang.Unroll

class AddExperimentToGroupE2ESpec extends BaseE2EIntegrationSpec {

    def "should create experiment group"() {
        given:
        def experiment1 = draftExperiment()
        def experiment2 = draftExperiment()
        def experiment3 = draftExperiment()

        when:
        def group = createExperimentGroupAndFetch([experiment1.id, experiment2.id, experiment3.id])

        then:
        group.experiments == [experiment1.id, experiment2.id, experiment3.id]
    }

    def "should not add active experiment to existing group"() {
        given:
        def experiment1 = draftExperiment()
        def experiment2 = startedExperiment()

        when:
        createExperimentGroup([experiment1.id, experiment2.id])

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should not add non-existing experiment to a group"(){
        when:
        createExperimentGroup(['non-existing'])

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.BAD_REQUEST

        when:
        def experiment1 = draftExperiment()
        createExperimentGroup([experiment1.id, 'non-existing'])

        then:
        exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should not create experiment group if there is not enough percentage space"() {
        given:
        def experiment1 = draftExperiment([percentage: 33])
        def experiment2 = draftExperiment([percentage: 34])

        when:
        createExperimentGroup([experiment1.id, experiment2.id])

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should not create experiment group if one of experiments is in another group"() {
        given:
        def experiment1 = draftExperiment()
        def experiment2 = draftExperiment()
        def experiment3 = draftExperiment()

        and:
        createExperimentGroup([experiment1.id, experiment2.id])

        when:
        createExperimentGroup([experiment1.id, experiment3.id])

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should set group's salt to first experiment"() {
        given:
        def experiment1 = startedExperiment()
        def experiment2 = draftExperiment()

        when:
        def group = createExperimentGroupAndFetch([experiment1.id, experiment2.id])

        then:
        group.salt == experiment1.id
    }

    def "should preserve predicates after adding experiment to group"() {
        given:
        def experiment1 = startedExperiment([
                deviceClass: 'desktop',
                internalVariantName: 'base',
                customParameterName: 'someCustomParam',
                customParameterValue: 'someCustomParamValue'
        ])
        def experiment2 = draftExperiment()

        when:
        createExperimentGroup([experiment1.id, experiment2.id])
        def experiment = fetchExperiment(experiment1.id)

        then:
        experiment.renderedVariants
                .every({variant ->
                            variant.predicates.any({predicate -> predicate.type == 'DEVICE_CLASS' && predicate.device == 'desktop'})})

        and:
        experiment.renderedVariants
                .find({it -> it.name == 'base'})
                .predicates.any({predicate -> predicate.type == 'INTERNAL'})

        and:
        experiment.renderedVariants
                .every({variant ->
                            variant.predicates.any({predicate ->
                                predicate.type == 'CUSTOM_PARAM' &&
                                        predicate.name == 'someCustomParam' &&
                                        predicate.value == 'someCustomParamValue'
                            })})
    }

    @Unroll
    def "should render single internal"() {
        given:
        def experiment1 = startedExperiment([
                internalVariantName: internalVariantName
        ])
        def experiment2 = draftExperiment()

        when:
        createExperimentGroup([experiment1.id, experiment2.id])

        then:
        fetchExperiment(experiment1.id).renderedVariants
                .count {v -> v.predicates.any({predicate -> predicate.type == 'INTERNAL'})} == 1

        where:
        internalVariantName << ['nonExistentVariant', 'base']
    }
}
