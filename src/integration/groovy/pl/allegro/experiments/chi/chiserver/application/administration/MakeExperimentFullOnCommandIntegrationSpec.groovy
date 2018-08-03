package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.CommandFactory
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCreationRequest
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentGroupCreationRequest
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

    def "should fail to make inactive experiment full-on"() {
        given: "an inactive experiment"
        def experiment = draftExperimentWithVariants(["v1", "v2"])

        when: "make experiment full-on command is executed"
        makeExperimentFullOnCommand(experiment.id, "v1").execute()

        then: "exception is thrown"
        def exception = thrown ExperimentCommandException
        exception.message == "Experiment is not ACTIVE. Now '${experiment.id}' has DRAFT status"

        and: "experiment is not in full-on mode"
        !experiment.isFullOn()
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
        !activeExperiment.isFullOn()

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

        and: "experiment is not in full-on mode"
        !experiment.isFullOn()

        where:
        description | groupCreator
        "a pair"    | ({ it, id -> it.createPairedExperimentWithVariants(["v3", "v4"], [id]) })
        "a group"   | ({ it, id -> it.createExperimentGroup([id, it.draftExperimentWithVariants(["v3", "v4"]).id]) })
    }

    private def makeExperimentFullOnCommand(String experimentId, String variantName) {
        return commandFactory.makeExperimentFullOnCommand(
                experimentId,
                new MakeExperimentFullOnProperties(variantName))
    }

    private def createExperimentGroup(List<String> experimentIds) {
        commandFactory
                .createExperimentGroupCommand(experimentGroupRequest(experimentIds))
                .execute()
    }

    private def createPairedExperimentWithVariants(List<String> variantNames, List<String> experimentIds) {
        def experimentId = UUID.randomUUID().toString()
        def request = new PairedExperimentCreationRequest(
                experimentRequestWithVariants(experimentId, variantNames),
                experimentGroupRequest([experimentId] + experimentIds)
        )
        commandFactory
                .createPairedExperimentCommand(request)
                .execute()
        return experimentsRepository
                .getExperiment(experimentId)
                .get()
    }

    private def startedExperimentWithVariants(List<String> variantNames) {
        def experiment = draftExperimentWithVariants(variantNames)
        def properties = new StartExperimentProperties(30)
        commandFactory
                .startExperimentCommand(experiment.id, properties)
                .execute()
        return experimentsRepository
                .getExperiment(experiment.id)
                .get()
    }

    private def draftExperimentWithVariants(List<String> variantNames) {
        def experimentId = UUID.randomUUID().toString()
        commandFactory
                .createExperimentCommand(experimentRequestWithVariants(experimentId, variantNames))
                .execute()
        return experimentsRepository
                .getExperiment(experimentId)
                .get()
    }

    private static def experimentRequestWithVariants(String id, List<String> variantNames) {
        return ExperimentCreationRequest.builder()
                .id(id)
                .variantNames(variantNames)
                .percentage(10)
                .reportingEnabled(true)
                .build()
    }

    private static def experimentGroupRequest(List<String> experimentIds) {
        def groupId = UUID.randomUUID().toString()
        return new ExperimentGroupCreationRequest(groupId, experimentIds)
    }
}
