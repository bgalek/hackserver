package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.*
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsTestConfig
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MutableUserProvider

import static pl.allegro.experiments.chi.chiserver.application.administration.CommandTestUtils.simpleExperimentRequest

@ContextConfiguration(classes = [ExperimentsTestConfig])
@DirtiesContext
class StartExperimentCommandIntegrationSpec extends BaseIntegrationSpec {
    @Autowired
    ExperimentsRepository experimentsRepository

    @Autowired
    MutableUserProvider mutableUserProvider

    PermissionsAwareExperimentRepository permissionsAwareExperimentGetter

    def setup() {
        permissionsAwareExperimentGetter = new PermissionsAwareExperimentRepository(experimentsRepository, mutableUserProvider)
    }

    def "should start experiment"() {
        given:
        def id = UUID.randomUUID().toString()
        mutableUserProvider.user = new User('Root', [], true)
        def command = new CreateExperimentCommand(experimentsRepository, mutableUserProvider, simpleExperimentRequest(id))
        command.execute()

        and:
        def startCommand = new StartExperimentCommand(
                experimentsRepository,
                new StartExperimentProperties(30),
                permissionsAwareExperimentGetter,
                id)

        when:
        startCommand.execute()

        then:
        experimentsRepository.getExperiment(id).status == ExperimentStatus.ACTIVE
    }

    def "should not start experiment if it is not DRAFT"() {
        given:
        def id = UUID.randomUUID().toString()
        mutableUserProvider.user = new User('Root', [], true)
        def command = new CreateExperimentCommand(experimentsRepository, mutableUserProvider, simpleExperimentRequest(id))
        command.execute()

        and:
        def startCommand = new StartExperimentCommand(
                experimentsRepository,
                new StartExperimentProperties(30),
                permissionsAwareExperimentGetter,
                id)
        startCommand.execute()

        and:
        def restartCommand = new StartExperimentCommand(
                experimentsRepository,
                new StartExperimentProperties(30),
                permissionsAwareExperimentGetter,
                id)

        when:
        restartCommand.execute()

        then:
        ExperimentCommandException e = thrown()
        e.message.startsWith("Experiment is not DRAFT")
    }

    def "should not start experiment when given number of days is negative or zero"() {
        when:
        new StartExperimentProperties(duration)

        then:
        thrown IllegalArgumentException

        where:
        duration << [0, -1]
    }
}