package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinition
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.ReportingType
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.CreateExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCreationRequest
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.StartExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.StartExperimentProperties
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.StopExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.UpdateExperimentEventDefinitionsCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsTestConfig
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MutableUserProvider

import static pl.allegro.experiments.chi.chiserver.application.administration.CommandTestUtils.simpleExperimentRequest

@ContextConfiguration(classes = [ExperimentsTestConfig])
@DirtiesContext
class UpdateEventDefinitionsCommandIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    ExperimentsRepository experimentsRepository

    @Autowired
    ExperimentGroupRepository experimentGroupRepository

    @Autowired
    MutableUserProvider mutableUserProvider

    PermissionsAwareExperimentRepository permissionsAwareExperimentGetter

    def setup() {
        permissionsAwareExperimentGetter = new PermissionsAwareExperimentRepository(experimentsRepository, mutableUserProvider)
    }

    def "should update event definitions"() {
        given:
        def experiment = experimentCreatedByRoot(true)

        and:
        def updateEventDefinitions = new UpdateExperimentEventDefinitionsCommand(
                experiment.id,
                experimentsRepository,
                permissionsAwareExperimentGetter,
                [
                        new EventDefinition('c1', 'a1', 'v1', 'l1', 'b1'),
                        new EventDefinition('c3', 'a3', 'v3', 'l3', 'b3')
                ]
        )

        when:
        updateEventDefinitions.execute()

        then:
        def updated = experimentsRepository.getExperiment(experiment.id).get()
        updated.definition.get().reportingDefinition.eventDefinitions == [
                new EventDefinition('c1', 'a1', 'v1', 'l1', 'b1'),
                new EventDefinition('c3', 'a3', 'v3', 'l3', 'b3')
        ]
        updated.definition.get().reportingDefinition.type == ReportingType.FRONTEND
    }

    def "should not update event definitions if experiment is ENDED"() {
        given:
        def experiment = experimentCreatedByRoot(true)

        and:
        def startCommand = new StartExperimentCommand(
                experimentsRepository,
                new StartExperimentProperties(30),
                permissionsAwareExperimentGetter,
                experimentGroupRepository,
                experiment.id)
        startCommand.execute()

        and:
        def stop = new StopExperimentCommand(
                experiment.id,
                experimentsRepository,
                permissionsAwareExperimentGetter)
        stop.execute()

        and:
        def updateEventDefinitions = new UpdateExperimentEventDefinitionsCommand(
                experiment.id,
                experimentsRepository,
                permissionsAwareExperimentGetter,
                [
                        new EventDefinition('c1', 'a1', 'v1', 'l1', 'b1'),
                        new EventDefinition('c3', 'a3', 'v3', 'l3', 'b3')
                ]
        )

        when:
        updateEventDefinitions.execute()

        then:
        ExperimentCommandException e = thrown()
        e.message.startsWith("ENDED experiment event definitions cant be updated")
    }

    def "should not update event definitions if experiment reporting type is not FRONTEND"() {
        given:
        def experiment = experimentCreatedByRoot(false)

        and:
        def updateEventDefinitions = new UpdateExperimentEventDefinitionsCommand(
                experiment.id,
                experimentsRepository,
                permissionsAwareExperimentGetter,
                [
                        new EventDefinition('c1', 'a1', 'v1', 'l1', 'b1'),
                        new EventDefinition('c3', 'a3', 'v3', 'l3', 'b3')
                ]
        )

        when:
        updateEventDefinitions.execute()

        then:
        ExperimentCommandException e = thrown()
        e.message.startsWith("Non frontend experiment has no event definitions")
    }

    Experiment experimentCreatedByRoot(boolean isFrontend) {
        def id = UUID.randomUUID().toString()
        mutableUserProvider.user = new User('Root', [], true)
        def command = new CreateExperimentCommand(
                experimentsRepository,
                mutableUserProvider,
                isFrontend ? simpleFrontendExperimentRequest(id) : simpleExperimentRequest(id))
        command.execute()
        return experimentsRepository.getExperiment(id).get()
    }

    ExperimentCreationRequest simpleFrontendExperimentRequest(String id) {
        def variantNames = []
        def internalVariantName = "v1"
        return new ExperimentCreationRequest(
                id,
                variantNames,
                internalVariantName,
                1,
                null,
                "simple description",
                "some link",
                ["group a", "group b"],
                true,
                [
                        new EventDefinition('c1', 'a1', 'v1', 'l1', 'b1'),
                        new EventDefinition('c2', 'a2', 'v2', 'l2', 'b2')
                ],
                ReportingType.FRONTEND)
    }
}
