package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.CreateExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.DeleteExperimentCommand
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

    PermissionsAwareExperimentRepository permissionsAwareExperimentGetter

    def setup() {
        statisticsRepository = Mock(StatisticsRepository)
        permissionsAwareExperimentGetter = new PermissionsAwareExperimentRepository(experimentsRepository, mutableUserProvider)
    }

    def "should delete experiment"() {
        given:
        def id = UUID.randomUUID().toString()
        mutableUserProvider.user = new User('Root', [], true)
        def command = new CreateExperimentCommand(experimentsRepository, mutableUserProvider, simpleExperimentRequest(id))
        command.execute()

        and:
        def deleteCommand = new DeleteExperimentCommand(experimentsRepository, permissionsAwareExperimentGetter, id, statisticsRepository)

        and:
        statisticsRepository.hasAnyStatistics(_) >> false

        when:
        deleteCommand.execute()

        then:
        !experimentsRepository.getExperiment(id).isPresent()
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
        ExperimentCommandException e = thrown()
        e.message.startsWith("Experiment with statistics cannot be deleted")
    }
}