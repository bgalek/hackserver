package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.Command
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.CommandFactory
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCreationRequest
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentGroupCreationRequest
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.MakeExperimentFullOnCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.MakeExperimentFullOnProperties
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PairedExperimentCreationRequest
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.StartExperimentProperties
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsTestConfig
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MutableUserProvider
import spock.lang.Unroll

@ContextConfiguration(classes = [ExperimentsTestConfig])
class MakeExperimentFullOnCommandIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    ExperimentsRepository experimentsRepository

    @Autowired
    MutableUserProvider mutableUserProvider

    @Autowired
    CommandFactory commandFactory

    def setup() {
        mutableUserProvider.user = new User('Root', [], true)
    }

    @Unroll
    def "should make internal variant full-on if internal variant #description"() {
        given:
        def experiment = startedExperimentWithVariants(["v1", "v2"], internalVariantName)

        when:
        makeExperimentFullOnCommand(experiment.id, internalVariantName).execute()
        experiment = experimentsRepository.getExperiment(experiment.id).get()

        then:
        experiment.isFullOn()
        experiment.definition.get().fullOnVariantName.get() == internalVariantName

        where:
        internalVariantName | description
        "v1"                | "is one of normal variants"
        "v3"                | "is not one of normal variants"
    }

    def "should fail to make nonexistent variant full-on"() {
        given: "an experiment"
        def experiment = startedExperimentWithVariants(["v1", "v2"])

        when: "a nonexistent variant is being made full-on"
        def nonexistentVariantName = "nonexistent variant"
        makeExperimentFullOnCommand(experiment.id, nonexistentVariantName).execute()

        then: "exception is thrown"
        def exception = thrown ExperimentCommandException
        exception.message == "Experiment '${experiment.id}' does not have variant named '$nonexistentVariantName'"

        and: "experiment is not in full-on mode"
        !experimentsRepository.getExperiment(experiment.id).get().isFullOn()
    }

    def "should fail to make inactive experiment full-on"() {
        given: "an inactive experiment"
        def experiment = draftExperimentWithVariants(["v1", "v2"])

        when: "make experiment full-on command is executed"
        makeExperimentFullOnCommand(experiment.id, "v1").execute()

        then: "exception is thrown"
        def exception = thrown ExperimentCommandException
        exception.message == "Experiment is not ACTIVE. Now '${experiment.id}' has DRAFT status"

        and: "experiment is not in full-on mode"
        !experimentsRepository.getExperiment(experiment.id).get().isFullOn()
    }

    @Unroll
    def "should fail to make #description experiment full-on"() {
        given: "#decription experiments"
        def activeExperiment = startedExperimentWithVariants(["v1", "v2"])
        groupCreator(this, activeExperiment.id)

        when: "make experiment full-on command is executed on one of the #description experiments"
        makeExperimentFullOnCommand(activeExperiment.id, "v1").execute()
        activeExperiment = experimentsRepository.getExperiment(activeExperiment.id).get()

        then: "exception is thrown"
        def exception = thrown ExperimentCommandException
        exception.message == "Experiment cannot be made full-on if it belongs to a group"

        and: "experiment is not in full-on mode"
        !experimentsRepository.getExperiment(activeExperiment.id).get().isFullOn()

        where:
        description | groupCreator
        "paired"    | ({ it, id -> it.createPairedExperimentWithVariants(["v3", "v4"], [id]) })
        "grouped"   | ({ it, id -> it.createExperimentGroup([id, it.draftExperimentWithVariants(["v3", "v4"]).id]) })
    }

    @Unroll
    def "should fail to add full-on experiment to #description"() {
        given: "a full-on experiment"
        def experiment = startedExperimentWithVariants(["v1", "v2"])
        makeExperimentFullOnCommand(experiment.id, "v1").execute()

        when: "experiment is added to #description"
        groupCreator(this, experiment.id)

        then: "exception is thrown"
        def exception = thrown ExperimentCommandException
        exception.message == "Cannot create group if one of the experiments is full-on"

        where:
        description | groupCreator
        "a pair"    | ({ it, id -> it.createPairedExperimentWithVariants(["v3", "v4"], [id]) })
        "a group"   | ({ it, id -> it.createExperimentGroup([id, it.draftExperimentWithVariants(["v3", "v4"]).id]) })
    }

    Command makeExperimentFullOnCommand(String experimentId, String variantName) {
        commandFactory.makeExperimentFullOnCommand(
                experimentId,
                new MakeExperimentFullOnProperties(variantName))
    }

    void createExperimentGroup(List<String> experimentIds) {
        commandFactory
                .createExperimentGroupCommand(experimentGroupRequest(experimentIds))
                .execute()
    }

    Experiment createPairedExperimentWithVariants(List<String> variantNames, List<String> experimentIds) {
        def experimentId = UUID.randomUUID().toString()
        def request = new PairedExperimentCreationRequest(
                experimentRequestWithVariants(experimentId, variantNames),
                experimentGroupRequest([experimentId] + experimentIds)
        )
        commandFactory
                .createPairedExperimentCommand(request)
                .execute()

        experimentsRepository
                .getExperiment(experimentId)
                .get()
    }

    Experiment startedExperimentWithVariants(List<String> variantNames, String internalVariantName = null) {
        def experiment = draftExperimentWithVariants(variantNames, internalVariantName)
        def properties = new StartExperimentProperties(30)
        commandFactory
                .startExperimentCommand(experiment.id, properties)
                .execute()
        experimentsRepository
                .getExperiment(experiment.id)
                .get()
    }

    Experiment draftExperimentWithVariants(List<String> variantNames, String internalVariantName = null) {
        def experimentId = UUID.randomUUID().toString()
        commandFactory
                .createExperimentCommand(experimentRequestWithVariants(experimentId, variantNames, internalVariantName))
                .execute()
        experimentsRepository
                .getExperiment(experimentId)
                .get()
    }

    ExperimentCreationRequest experimentRequestWithVariants(String id, List<String> variantNames, String internalVariantName = null) {
        ExperimentCreationRequest.builder()
                .id(id)
                .variantNames(variantNames)
                .internalVariantName(internalVariantName)
                .percentage(10)
                .build()
    }

    ExperimentGroupCreationRequest experimentGroupRequest(List<String> experimentIds) {
        new ExperimentGroupCreationRequest(UUID.randomUUID().toString(), experimentIds)
    }
}
