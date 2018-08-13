package pl.allegro.experiments.chi.chiserver.application.commands

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus
import pl.allegro.experiments.chi.chiserver.utils.CommandExperimentUtils
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.CommandFactory
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentActions
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MutableUserProvider

@ContextConfiguration(classes = CommandTestConfig.class)
abstract class BaseCommandIntegrationSpec extends BaseIntegrationSpec implements CommandExperimentUtils {

    @Autowired
    InMemoryStatisticsRepository statisticsRepository

    @Autowired
    MutableUserProvider mutableUserProvider

    @Autowired
    ExperimentGroupRepository experimentGroupRepository

    @Autowired
    ExperimentsRepository experimentsRepository

    @Autowired
    PermissionsAwareExperimentRepository permissionsAwareExperimentRepository

    @Autowired
    CommandFactory commandFactory

    @Autowired
    ExperimentActions experimentActions

    User ROOT_USER = new User('Root', [], true)

    def setup() {
        signInAs(ROOT_USER)
    }

    def cleanup() {
        experimentsRepository.getAll().forEach { experimentsRepository.delete(it.id) }
    }

    void signInAs(User user) {
        mutableUserProvider.user = user
    }

    List<ExperimentStatus> allExperimentStatusValuesExcept(ExperimentStatus... statuses) {
        ExperimentStatus.values()
                .findAll { !statuses.contains(it) }
                .findAll { it != ExperimentStatus.PLANNED }
    }
}
