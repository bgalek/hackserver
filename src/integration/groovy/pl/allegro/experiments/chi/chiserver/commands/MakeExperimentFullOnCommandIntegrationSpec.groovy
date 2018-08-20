package pl.allegro.experiments.chi.chiserver.commands

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException
import spock.lang.Unroll

import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.*

class MakeExperimentFullOnCommandIntegrationSpec extends BaseCommandIntegrationSpec {

    def "should make ACTIVE experiment full-on"() {
        given:
        def experiment = startedExperiment([variantNames: ["v1", "v2"]])

        when:
        makeExperimentFullOn(experiment.id, "v1")

        then:
        fetchExperiment(experiment.id).isFullOn()
    }

    @Unroll
    def "should make internal variant full-on if internal variant #description"() {
        given:
        def experiment = startedExperiment([variantNames: ["v1", "v2"], internalVariantName: internalVariantName])

        when:
        makeExperimentFullOn(experiment.id, internalVariantName)

        then:
        fetchExperiment(experiment.id).isFullOn()
        fetchExperimentDefinition(experiment.id).fullOnVariantName.get() == internalVariantName

        where:
        internalVariantName | description
        "v1"                | "is one of normal variants"
        "v3"                | "is not one of normal variants"
    }

    def "should not make nonexistent variant full-on"() {
        given:
        def experiment = startedExperiment([variantNames: ["v1", "v2"]])

        when:
        def nonexistentVariantName = "nonexistent variant"
        makeExperimentFullOn(experiment.id, nonexistentVariantName)

        then:
        def exception = thrown ExperimentCommandException
        exception.message == "Experiment '${experiment.id}' does not have variant named '$nonexistentVariantName'"

        and:
        !experiment.isFullOn()
    }

    @Unroll
    def "should not make #status experiment full-on"() {
        given:
        def experiment = experimentWithStatus(status, [variantNames: ["v1", "v2"]])

        when:
        makeExperimentFullOn(experiment.id, "v1")

        then:
        def exception = thrown ExperimentCommandException
        exception.message == "Experiment is not ACTIVE. Now '${experiment.id}' has $status status"

        and:
        fetchExperiment(experiment.id).status == status

        where:
        status << allExperimentStatusValuesExcept(ACTIVE)
    }

    def "should not make grouped experiment full-on"() {
        given:
        def experiment = startedExperiment([variantNames: ["v1", "v2"]])
        createExperimentGroup([experiment.id, draftExperiment().id])

        when:
        makeExperimentFullOn(experiment.id, "v1")

        then:
        def exception = thrown ExperimentCommandException
        exception.message == "Experiment cannot be made full-on if it belongs to a group"

        and:
        !fetchExperiment(experiment.id).isFullOn()
    }

    def "should not add full-on experiment to a group"() {
        given:
        def experiment = fullOnExperiment([variantNames: ["v1", "v2"]], "v1")

        when:
        createExperimentGroup([experiment.id, draftExperiment().id])

        then:
        def exception = thrown ExperimentCommandException
        exception.message == "Cannot create group if one of the experiments is full-on"

        and:
        !experimentGroupRepository.experimentInGroup(experiment.id)
    }
}
