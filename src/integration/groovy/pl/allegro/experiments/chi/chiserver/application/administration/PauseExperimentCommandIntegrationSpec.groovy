package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentNotFoundException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsTestConfig
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MongoExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MutableUserProvider
import spock.lang.Shared

@ContextConfiguration(classes = [ExperimentsTestConfig])
@DirtiesContext
class PauseExperimentCommandIntegrationSpec extends BaseIntegrationSpec implements PreparedExperiments {

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

    def "should pause experiment"() {
        given:
        Experiment experiment = startedExperiment()

        and:
        def pauseCommand = this.pauseCommand(experiment.id)

        when:
        pauseCommand.execute()
        experiment = experimentsRepository.getExperiment(experiment.id).get()

        then:
        experiment.isPaused()
    }

    def "should not pause nonexistent experiment"() {
        when:
        pauseCommand('nonexistentExperimentId').execute()

        then:
        thrown ExperimentNotFoundException
    }

    def "should not pause experiment if it has ENDED status"() {
        when:
        pauseCommand(endedExperiment().id).execute()

        then:
        ExperimentCommandException e = thrown()
        e.message.startsWith("Experiment is not ACTIVE")
    }

    def "should not pause experiment if it is has PAUSED status"() {
        when:
        pauseCommand(pausedExperiment().id).execute()

        then:
        ExperimentCommandException e = thrown()
        e.message.startsWith("Experiment is not ACTIVE")

    }

    def "should not pause experiment if it has DRAFT status"() {
        when:
        pauseCommand(pausedExperiment().id).execute()

        then:
        ExperimentCommandException e = thrown()
        e.message.startsWith("Experiment is not ACTIVE")
    }
}
