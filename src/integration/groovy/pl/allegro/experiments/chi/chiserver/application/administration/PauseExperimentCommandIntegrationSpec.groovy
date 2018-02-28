package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentNotFoundException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create.CreateExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.pause.PauseExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start.StartExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start.StartExperimentProperties
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.stop.StopExperimentCommand
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsTestConfig
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MongoExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MutableUserProvider
import spock.lang.Shared


import static pl.allegro.experiments.chi.chiserver.application.administration.CommandTestUtils.simpleExperimentRequest

@ContextConfiguration(classes = [ExperimentsTestConfig])
@DirtiesContext
class PauseExperimentCommandIntegrationSpec extends BaseIntegrationSpec {

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

    def "should pause experiment"() {
        given:
        def experiment = startedExperiment()

        and:
        def pauseCommand = this.pauseCommand(experiment.id)

        when:
        pauseCommand.execute()
        experiment = experimentsRepository.getExperiment(experiment.id)

        then:
        experiment.isPaused()
    }

    def "should not pause nonexistent experiment"() {
        when:
        pauseCommand('nonexistentExperimentId').execute()

        then:
        thrown ExperimentNotFoundException
    }

    def "should not pause experiment if it is ENDED"() {
        when:
        pauseCommand(endedExperiment().id).execute()

        then:
        thrown ExperimentCommandException
    }

    def startedExperiment() {
        def experiment = draftExperiment()
        def properties = new StartExperimentProperties(30)
        startExperimentCommand(experiment.id, properties)
                .execute()

        return experimentsRepository.getExperiment(experiment.id)
    }

    def endedExperiment() {
        def experiment = startedExperiment()
        stopExperimentCommand(experiment.id)
                .execute()

        return experimentsRepository.getExperiment(experiment.id)
    }

    def startExperimentCommand(String experimentId, StartExperimentProperties properties) {
        return new StartExperimentCommand(
                experimentsRepository,
                properties,
                permissionsAwareExperimentGetter,
                experimentId)
    }

    def stopExperimentCommand(String experimentId) {
        return new StopExperimentCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentGetter)
    }

    Experiment draftExperiment() {
        def id = UUID.randomUUID().toString()
        mutableUserProvider.user = new User('Root', [], true)
        def command = new CreateExperimentCommand(
                experimentsRepository,
                mutableUserProvider,
                simpleExperimentRequest(id))
        command.execute()
        experimentsRepository.getExperiment(id)
    }

    PauseExperimentCommand pauseCommand(String experimentId) {
        new PauseExperimentCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentGetter)
    }

}
