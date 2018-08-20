package pl.allegro.experiments.chi.chiserver

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.CommandFactory
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentActions
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicStatisticsForVariantMetricRepository
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.StatisticsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MutableUserProvider
import spock.lang.Specification

@SpringBootTest(
        classes = AppRunner.class,
        properties = "application.environment=integration",
        webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class BaseIntegrationSpec extends Specification {

    @Value('${local.server.port}')
    int port

    @Autowired
    ClassicStatisticsForVariantMetricRepository classicStatisticsForVariantMetricRepository

    @Autowired
    StatisticsRepository statisticsRepository

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
        experimentsRepository.getAll().findAll{it.origin == "MONGO"}.forEach { experimentsRepository.delete(it.id) }
    }

    void signInAs(User user) {
        mutableUserProvider.user = user
    }

    static List<ExperimentStatus> allExperimentStatusValuesExcept(ExperimentStatus... statuses) {
        ExperimentStatus.values()
                .findAll { !statuses.contains(it) }
                .findAll { it != ExperimentStatus.PLANNED }
    }
}
