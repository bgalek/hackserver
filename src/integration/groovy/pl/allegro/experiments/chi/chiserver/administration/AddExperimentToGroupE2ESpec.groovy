package pl.allegro.experiments.chi.chiserver.administration

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus
import spock.lang.Unroll

import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.*

class AddExperimentToGroupE2ESpec extends BaseE2EIntegrationSpec {

    def "should create experiment group"() {
        given:
        def experiment1 = startedExperiment()
        def experiment2 = draftExperiment()
        def experiment3 = draftExperiment()

        when:
        def group = createExperimentGroupAndFetch([experiment1.id, experiment2.id, experiment3.id])

        then:
        group.experiments == [experiment1.id, experiment2.id, experiment3.id]
    }

    @Unroll
    def "should add #status experiment to a new group"() {
        given:
        def experiment = experimentWithStatus(status)

        when:
        def group = createExperimentGroup([experiment.id])

        then:
        def freshExperiment = fetchExperiment(experiment.id)
        freshExperiment.experimentGroup.id == group

        where:
        status << [ACTIVE, PAUSED, DRAFT]
    }

    @Unroll
    def "should prevent from adding #status experiment to a new group"() {
        given:
        def experiment = experimentWithStatus(status)

        when:
        createExperimentGroup([experiment.id])

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.BAD_REQUEST

        where:
        status << [ENDED, FULL_ON]
    }

    @Unroll
    def "should prevent from adding #status experiment to the existing group"() {
        given:
        def experiment1 = draftExperiment()
        def experiment2 = experimentWithStatus(status)

        when:
        createExperimentGroup([experiment1.id, experiment2.id])

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.BAD_REQUEST

        where:
        status << allExperimentStatusValuesExcept(DRAFT)
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

    @Unroll
    def "should set group's salt to Id of the first experiment when it's #status"() {
        given:
        def experiment1 = experimentWithStatus(status)
        def experiment2 = draftExperiment()

        when:
        def group = createExperimentGroupAndFetch([experiment1.id, experiment2.id])

        then:
        group.salt == experiment1.id

        where:
        status << [ExperimentStatus.ACTIVE, ExperimentStatus.PAUSED]
    }

    def "should set group's salt to random UUID when the first experiment is draft"() {
        given:
        def experiment1 = draftExperiment()
        def experiment2 = draftExperiment()

        when:
        def group = createExperimentGroupAndFetch([experiment1.id, experiment2.id])

        then:
        group.salt != experiment1.id
        group.salt != experiment2.id
        group.salt.size() == 36
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
