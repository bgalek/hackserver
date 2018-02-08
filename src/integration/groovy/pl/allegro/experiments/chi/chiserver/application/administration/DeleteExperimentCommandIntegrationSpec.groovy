package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.application.experiments.administration.AuthorizationException
import pl.allegro.experiments.chi.chiserver.application.experiments.administration.CreateExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentNotFoundException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete.DeleteExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete.DeleteExperimentException
import pl.allegro.experiments.chi.chiserver.domain.statistics.StatisticsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsTestConfig
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.FileBasedExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MutableUserProvider

import static pl.allegro.experiments.chi.chiserver.application.administration.CommandTestUtils.simpleExperimentRequest

@ContextConfiguration(classes = [ExperimentsTestConfig])
@DirtiesContext
class DeleteExperimentCommandIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    ExperimentsRepository experimentsRepository

    @Autowired
    FileBasedExperimentsRepository fileBasedExperimentsRepository

    StatisticsRepository statisticsRepository

    @Autowired
    MutableUserProvider mutableUserProvider

    PermissionsAwareExperimentGetter permissionsAwareExperimentGetter

    def setup() {
        statisticsRepository = Mock(StatisticsRepository)
        permissionsAwareExperimentGetter = new PermissionsAwareExperimentGetter(experimentsRepository, mutableUserProvider)
    }

    def "should delete experiment"() {
        given:
        def id = UUID.randomUUID().toString()
        mutableUserProvider.user = new User('Root', [], true)
        def command = new CreateExperimentCommand(experimentsRepository, mutableUserProvider, simpleExperimentRequest(id))
        command.execute()

        and:
        mutableUserProvider.user = user
        def deleteCommand = new DeleteExperimentCommand(experimentsRepository, permissionsAwareExperimentGetter, id, statisticsRepository)

        and:
        statisticsRepository.hasAnyStatistics(_) >> false

        when:
        deleteCommand.execute()

        then:
        experimentsRepository.getExperiment(id) == null

        where:
        user << [new User('Root', [], true),
                 new User('Normal', ['group a'], false),
                 new User('Normal', ['nonexistent', 'group a'], false),
                 new User('Root with group', ['group a'], true)]
    }

    def "should not delete nonexistent experiment"() {
        given:
        def id = UUID.randomUUID().toString()
        mutableUserProvider.user = new User('Root', [], true)

        and:
        def deleteCommand = new DeleteExperimentCommand(experimentsRepository, permissionsAwareExperimentGetter, id, statisticsRepository)

        and:
        statisticsRepository.hasAnyStatistics(_) >> false

        when:
        deleteCommand.execute()

        then:
        thrown ExperimentNotFoundException
    }

    def "should not delete experiment with statistics"() {
        given:
        def id = UUID.randomUUID().toString()
        mutableUserProvider.user = new User('Root', [], true)
        def command = new CreateExperimentCommand(experimentsRepository, mutableUserProvider, simpleExperimentRequest(id))
        command.execute()

        and:
        def deleteCommand = new DeleteExperimentCommand(experimentsRepository, permissionsAwareExperimentGetter, id, statisticsRepository)

        and:
        statisticsRepository.hasAnyStatistics(_) >> true

        when:
        deleteCommand.execute()

        then:
        thrown DeleteExperimentException
    }

    def "should not delete experiment when user has no permissions"() {
        given:
        def id = UUID.randomUUID().toString()
        mutableUserProvider.user = new User('Root', [], true)
        def command = new CreateExperimentCommand(experimentsRepository, mutableUserProvider, simpleExperimentRequest(id))
        command.execute()

        and:
        mutableUserProvider.user = user
        def deleteCommand = new DeleteExperimentCommand(experimentsRepository, permissionsAwareExperimentGetter, id, statisticsRepository)

        and:
        statisticsRepository.hasAnyStatistics(_) >> false

        when:
        deleteCommand.execute()

        then:
        thrown AuthorizationException

        where:
        user << [
                new User('NotAuthor', ['some group'], false),
                new User('NotAuthor', [], false)
        ]
    }
}