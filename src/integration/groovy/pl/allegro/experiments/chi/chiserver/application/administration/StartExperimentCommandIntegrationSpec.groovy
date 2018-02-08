package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.application.experiments.administration.AuthorizationException
import pl.allegro.experiments.chi.chiserver.application.experiments.administration.CreateExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentNotFoundException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start.StartExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start.StartExperimentException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start.StartExperimentProperties
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

    PermissionsAwareExperimentGetter permissionsAwareExperimentGetter

    def setup() {
        permissionsAwareExperimentGetter = new PermissionsAwareExperimentGetter(experimentsRepository, mutableUserProvider)
    }

    def "should start experiment"() {
        given:
        def id = UUID.randomUUID().toString()
        mutableUserProvider.user = new User('Root', [], true)
        def command = new CreateExperimentCommand(experimentsRepository, mutableUserProvider, simpleExperimentRequest(id))
        command.execute()

        and:
        mutableUserProvider.user = user
        def startCommand = new StartExperimentCommand(
                experimentsRepository,
                new StartExperimentProperties(30),
                permissionsAwareExperimentGetter,
                id)

        when:
        startCommand.execute()

        then:
        experimentsRepository.getExperiment(id).status == ExperimentStatus.ACTIVE

        where:
        user << [new User('Root', [], true),
                 new User('Normal', ['group a'], false),
                 new User('Normal', ['nonexistent', 'group a'], false),
                 new User('Root with group', ['group a'], true)]
    }

    def "should not start nonexistent experiment"() {
        given:
        def id = UUID.randomUUID().toString()
        def startCommand = new StartExperimentCommand(
                experimentsRepository,
                new StartExperimentProperties(30),
                permissionsAwareExperimentGetter,
                id)

        when:
        startCommand.execute()

        then:
        thrown ExperimentNotFoundException
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
        thrown StartExperimentException
    }

    def "should not start experiment when user has no permissions"() {
        given:
        def id = UUID.randomUUID().toString()
        mutableUserProvider.user = new User('Author', [], true)
        def command = new CreateExperimentCommand(experimentsRepository, mutableUserProvider, simpleExperimentRequest(id))
        command.execute()

        and:
        mutableUserProvider.user = user
        def startCommand = new StartExperimentCommand(
                experimentsRepository,
                new StartExperimentProperties(30),
                permissionsAwareExperimentGetter,
                id)

        when:
        startCommand.execute()

        then:
        thrown AuthorizationException

        where:
        user << [
                new User('NotAuthor', ['some group'], false),
                new User('NotAuthor', [], false)
        ]
    }

    def "should not start experiment when given number of days is negative or zero"() {
        given:
        def id = UUID.randomUUID().toString()
        mutableUserProvider.user = new User('Root', [], true)
        def command = new CreateExperimentCommand(experimentsRepository, mutableUserProvider, simpleExperimentRequest(id))
        command.execute()

        and:
        def startCommand = new StartExperimentCommand(
                experimentsRepository,
                new StartExperimentProperties(duration),
                permissionsAwareExperimentGetter,
                id)

        when:
        startCommand.execute()

        then:
        thrown StartExperimentException

        where:
        duration << [0, -1]
    }
}