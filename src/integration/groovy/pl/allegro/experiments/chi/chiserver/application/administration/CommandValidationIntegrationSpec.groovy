package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.ActivityPeriod
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.AuthorizationException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentNotFoundException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.CreateExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ProlongExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ProlongExperimentProperties
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.StartExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.StartExperimentProperties
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.StopExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsTestConfig
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MongoExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MutableUserProvider
import spock.lang.Shared

import java.time.ZonedDateTime

import static pl.allegro.experiments.chi.chiserver.application.administration.CommandTestUtils.simpleExperimentRequest

@ContextConfiguration(classes = [ExperimentsTestConfig])
@DirtiesContext
class CommandValidationIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    MongoExperimentsRepository experimentsRepository

    @Autowired
    ExperimentGroupRepository experimentGroupRepository

    @Autowired
    MutableUserProvider mutableUserProvider

    @Shared
    PermissionsAwareExperimentRepository permissionsAwareExperimentGetter

    def setup() {
        permissionsAwareExperimentGetter = new PermissionsAwareExperimentRepository(
                experimentsRepository,
                mutableUserProvider)
    }

    def "should execute command when user has permissions"() {
        given:
        mutableUserProvider.user = user

        when:
        startCommand(draftExperiment().id).execute()

        then:
        true

        when:
        prolongCommand(startedExperiment().id).execute()

        then:
        true

        when:
        stopCommand(startedExperiment().id).execute()

        then:
        true

        where:
        user << [
                new User('Root', [], true),
                new User('Normal', ['group a'], false),
                new User('RootAuthor', [], false), // owner
                new User('Normal', ['nonexistent', 'group a'], false),
                new User('Root with group', ['group a'], true)
        ]
    }

    def "should not execute command when user has no permissions"() {
        given:
        mutableUserProvider.user = user

        when:
        startCommand(draftExperiment().id).execute()

        then:
        thrown AuthorizationException

        when:
        prolongCommand(startedExperiment().id).execute()

        then:
        thrown AuthorizationException

        when:
        stopCommand(startedExperiment().id).execute()

        then:
        thrown AuthorizationException

        where:
        user << [
                new User('Other', [], false),
                new User('Other', ['unknown group'], false)
        ]
    }

    def "should not execute command when experiment does not exist"() {
        when:
        startCommand('nonexistent').execute()

        then:
        thrown ExperimentNotFoundException

        when:
        prolongCommand('nonexistent').execute()

        then:
        thrown ExperimentNotFoundException

        when:
        stopCommand('nonexistent').execute()

        then:
        thrown ExperimentNotFoundException
    }

    Experiment draftExperiment() {
        def prevUser = mutableUserProvider.user
        mutableUserProvider.user = new User('RootAuthor', [], true)
        def id = UUID.randomUUID().toString()
        def command = new CreateExperimentCommand(
                experimentsRepository,
                mutableUserProvider,
                simpleExperimentRequest(id))
        command.execute()
        mutableUserProvider.user = prevUser
        experimentsRepository.getExperiment(id).get()
    }

    def startedExperiment() {
        def prevUser = mutableUserProvider.user
        mutableUserProvider.user = new User('RootAuthor', [], true)
        def experiment = draftExperiment()
        def startCommand = new StartExperimentCommand(
                experimentsRepository,
                new StartExperimentProperties(30),
                permissionsAwareExperimentGetter,
                experimentGroupRepository,
                experiment.id)
        startCommand.execute()
        mutableUserProvider.user = prevUser
        experimentsRepository.getExperiment(experiment.id).get()
    }

    def endedExperiment() {
        def prevUser = mutableUserProvider.user
        mutableUserProvider.user = new User('RootAuthor', [], true)
        def started = startedExperiment()
        def stopCommand = stopCommand(started.id)
        stopCommand.execute()
        mutableUserProvider.user = prevUser
        experimentsRepository.getExperiment(started.id).get()
    }

    def plannedExperiment() {
        def prevUser = mutableUserProvider.user
        mutableUserProvider.user = new User('RootAuthor', [], true)
        def draft = draftExperiment()
        experimentsRepository.save(draft.mutate()
                .activityPeriod(new ActivityPeriod(
                        ZonedDateTime.now().plusDays(10),
                        ZonedDateTime.now().plusDays(20)
                ))
                .build())
        mutableUserProvider.user = prevUser
        experimentsRepository.getExperiment(draft.id).get()
    }

    StopExperimentCommand stopCommand(String experimentId) {
        new StopExperimentCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentGetter)
    }

    StartExperimentCommand startCommand(String experimentId) {
        new StartExperimentCommand(
                experimentsRepository,
                new StartExperimentProperties(30),
                permissionsAwareExperimentGetter,
                experimentGroupRepository,
                experimentId
        )
    }

    ProlongExperimentCommand prolongCommand(String experimentId) {
        new ProlongExperimentCommand(
                experimentsRepository,
                new ProlongExperimentProperties(30),
                permissionsAwareExperimentGetter,
                experimentId
        )
    }

}