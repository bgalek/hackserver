package pl.allegro.experiments.chi.chiserver.commands

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException

import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.*

class UpdateVariantsCommandIntegrationSpec extends BaseCommandIntegrationSpec {

    def "should change #status experiment's variants configuration"() {
        given:
        def experiment = experimentWithStatus(status)

        when:
        updateExperimentVariants(experiment.id, 'iname', 13, 'phone')

        then:
        def definition = fetchExperiment(experiment.id)
        definition.deviceClass.get().name() == 'phone'
        definition.internalVariantName.get() == 'iname'
        definition.variantNames == ['base', 'v1']
        definition.percentage.get() == 13

        where:
        status << allExperimentStatusValuesExcept(ENDED, FULL_ON)
    }

    def "should not change #status experiment's variants configuration"() {
        given:
        def experiment = experimentWithStatus(status)

        when:
        updateExperimentVariants(experiment.id, 'iname', 13, 'phone')

        then:
        def exception = thrown ExperimentCommandException
        exception.message == "$status experiment variants cant be updated"

        where:
        status << [ENDED, FULL_ON]
    }
}