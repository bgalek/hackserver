package pl.allegro.experiments.chi.chiserver.administration

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec

class CreateExperimentGroupE2ESpec extends BaseE2EIntegrationSpec {

    def "should create experiment group"() {
        given:
        def experiment1 = draftExperiment()
        def experiment2 = draftExperiment()

        when:
        def group = experimentGroup([experiment1.id, experiment2.id])

        then:
        group.experiments == [experiment1.id, experiment2.id]
    }

    def "should not create experiment group if it contains more than 1 active experiment"() {
        given:
        def experiment1 = startedExperiment()
        def experiment2 = startedExperiment()

        when:
        createExperimentGroup([experiment1.id, experiment2.id])

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should not create experiment group if contains #numberOfExperiments experiments"() {
        given:
        def experiments = (0..<numberOfExperiments).collect { draftExperiment() }
        def experimentIds = experiments.collect { it.id }

        when:
        createExperimentGroup(experimentIds as List<String>)

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.BAD_REQUEST

        where:
        numberOfExperiments << [0, 1]
    }

    def "should not create experiment group if one of provided experiments does not exist"() {
        given:
        def experiment = draftExperiment()

        when:
        createExperimentGroup([experiment.id, "nonexistent experiment id"])

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should not create experiment group if group name is not unique"() {
        given:
        def group = experimentGroup([draftExperiment().id, draftExperiment().id])

        when:
        createExperimentGroup([draftExperiment().id, draftExperiment().id], group.id as String)

        then:
        def exception = thrown HttpClientErrorException
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

    def "should set group's salt to first ACTIVE experiment"() {
        given:
        def experiment1 = startedExperiment()
        def experiment2 = draftExperiment()

        when:
        def group = experimentGroup([experiment1.id, experiment2.id])

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
        experimentGroup([experiment1.id, experiment2.id])
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
}
