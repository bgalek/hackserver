package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create.CreateExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong.ProlongExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong.ProlongExperimentException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong.ProlongExperimentProperties
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start.StartExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start.StartExperimentProperties
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsTestConfig
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MutableUserProvider

import static pl.allegro.experiments.chi.chiserver.application.administration.CommandTestUtils.simpleExperimentRequest

@ContextConfiguration(classes = [ExperimentsTestConfig])
@DirtiesContext
class ProlongExperimentCommandIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    ExperimentsRepository experimentsRepository

    @Autowired
    MutableUserProvider mutableUserProvider

    PermissionsAwareExperimentRepository permissionsAwareExperimentGetter

    def setup() {
        permissionsAwareExperimentGetter = new PermissionsAwareExperimentRepository(experimentsRepository, mutableUserProvider)
    }

    def "should prolong experiment"() {
        given:
        def experiment = experimentCreatedByRoot()

        and:
        def startCommand = new StartExperimentCommand(
                experimentsRepository,
                new StartExperimentProperties(30),
                permissionsAwareExperimentGetter,
                experiment.id)
        startCommand.execute()

        and:
        experiment = experimentsRepository.getExperiment(experiment.id)

        and:
        def prolongCommand = new ProlongExperimentCommand(
                experimentsRepository,
                new ProlongExperimentProperties(30),
                permissionsAwareExperimentGetter,
                experiment.id)

        when:
        prolongCommand.execute()

        then:
        experimentsRepository.getExperiment(experiment.id).activityPeriod.activeTo == experiment.activityPeriod.activeTo.plusDays(30)
        experimentsRepository.getExperiment(experiment.id).activityPeriod.activeFrom == experiment.activityPeriod.activeFrom
    }

    def "should not prolong experiment if it is not ACTIVE"() {
        given:
        def experiment = experimentCreatedByRoot()

        and:
        def prolongCommand = new ProlongExperimentCommand(
                experimentsRepository,
                new ProlongExperimentProperties(30),
                permissionsAwareExperimentGetter,
                experiment.id)

        when:
        prolongCommand.execute()

        then:
        thrown ProlongExperimentException
    }

    def "should not prolong experiment when given number of days is negative or zero"() {
        given:
        def experiment = experimentCreatedByRoot()

        and:
        def prolongCommand = new ProlongExperimentCommand(
                experimentsRepository,
                new ProlongExperimentProperties(30),
                permissionsAwareExperimentGetter,
                experiment.id)

        when:
        prolongCommand.execute()

        then:
        thrown ProlongExperimentException

        where:
        duration << [0, -1]
    }

    Experiment experimentCreatedByRoot() {
        def id = UUID.randomUUID().toString()
        mutableUserProvider.user = new User('Root', [], true)
        def command = new CreateExperimentCommand(experimentsRepository, mutableUserProvider, simpleExperimentRequest(id))
        command.execute()
        return experimentsRepository.getExperiment(id)
    }
}