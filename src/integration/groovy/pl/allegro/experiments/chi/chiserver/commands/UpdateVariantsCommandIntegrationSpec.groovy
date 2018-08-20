package pl.allegro.experiments.chi.chiserver.commands

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException

import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.*

class UpdateVariantsCommandIntegrationSpec extends BaseCommandIntegrationSpec {

    def "should change #status experiment's variants configuration"() {
        given:
        def experiment = experimentWithStatus(status)

        when:
        updateExperimentVariants(experiment.id, ['va', 'vb'], 'iname', 13, 'phone')

        then:
        def definition = fetchExperimentDefinition(experiment.id)
        definition.deviceClass.name() == 'phone'
        definition.internalVariantName.get() == 'iname'
        definition.variantNames == ['va', 'vb']
        definition.percentage.get() == 13

        where:
        status << allExperimentStatusValuesExcept(ENDED, FULL_ON)
    }

    def "should not change #status experiment's variants configuration"() {
        given:
        def experiment = experimentWithStatus(status)

        when:
        updateExperimentVariants(experiment.id, ['va', 'vb'], 'iname', 13, 'phone')

        then:
        def exception = thrown ExperimentCommandException
        exception.message == "$status experiment variants cant be updated"

        where:
        status << [ENDED, FULL_ON]
    }

    def "should not change grouped experiment's variants configuration"() {
        given:
        def experiment = startedExperiment()
        createExperimentGroup([experiment.id, draftExperiment().id])

        when:
        updateExperimentVariants(experiment.id, ['va', 'vb'], 'iname', 13, 'phone')

        then:
        def exception = thrown ExperimentCommandException
        exception.message == "Can not change variants of experiment bounded to a group"
    }
}