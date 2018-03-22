package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.*
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.StopExperimentCommand
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsTestConfig
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MongoExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MutableUserProvider
import spock.lang.Shared

import static pl.allegro.experiments.chi.chiserver.application.administration.CommandTestUtils.simpleExperimentRequest

@ContextConfiguration(classes = [ExperimentsTestConfig])
@DirtiesContext
class StopExperimentCommandIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    MongoExperimentsRepository experimentsRepository

    @Autowired
    MutableUserProvider mutableUserProvider

    @Shared
    PermissionsAwareExperimentRepository permissionsAwareExperimentGetter

    def setup() {
        permissionsAwareExperimentGetter = new PermissionsAwareExperimentRepository(
                experimentsRepository,
                mutableUserProvider)
    }

    def "should stop experiment"() {
        given:
        def experiment = startedExperiment()

        and:
        def stopCommand = stopCommand(experiment.id)

        when:
        stopCommand.execute()
        experiment = experimentsRepository.getExperiment(experiment.id).get()

        then:
        experiment.isEnded()
    }

    def "should not stop nonexistent experiment"() {
        when:
        stopCommand('nonexistentExperimentId').execute()

        then:
        thrown ExperimentNotFoundException
    }

    def "should not stop experiment if it is not ACTIVE"() {
        when:
        stopCommand(draftExperiment().id).execute()

        then:
        ExperimentCommandException e = thrown()
        e.message.startsWith("Experiment is not ACTIVE")
    }

    def startedExperiment() {
        def experiment = draftExperiment()
        def startCommand = new StartExperimentCommand(
                experimentsRepository,
                new StartExperimentProperties(30),
                permissionsAwareExperimentGetter,
                experiment.id)
        startCommand.execute()
        return experimentsRepository.getExperiment(experiment.id).get()
    }

    Experiment draftExperiment() {
        def id = UUID.randomUUID().toString()
        mutableUserProvider.user = new User('Root', [], true)
        def command = new CreateExperimentCommand(
                experimentsRepository,
                mutableUserProvider,
                simpleExperimentRequest(id))
        command.execute()
        experimentsRepository.getExperiment(id).get()
    }

    StopExperimentCommand stopCommand(String experimentId) {
        new StopExperimentCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentGetter)
    }

}
