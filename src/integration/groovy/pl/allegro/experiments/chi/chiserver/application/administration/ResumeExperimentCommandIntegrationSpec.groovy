package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentNotFoundException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsTestConfig
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MongoExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MutableUserProvider
import spock.lang.Shared

@ContextConfiguration(classes = [ExperimentsTestConfig])
@DirtiesContext
class ResumeExperimentCommandIntegrationSpec extends BaseIntegrationSpec implements PreparedExperiments {

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

    def "should resume experiment"() {
        given:
        Experiment experiment = pausedExperiment()

        and:
        def resumeCommand = resumeCommand(experiment.id)

        when:
        resumeCommand.execute()
        experiment = experimentsRepository.getExperiment(experiment.id).get()

        then:
        experiment.isActive()
    }

    def "should not resume non existing experiment"() {
        when:
        resumeCommand('nonexistentExperimentId').execute()

        then:
        thrown ExperimentNotFoundException
    }

    def "should not resume ENDED experiment"() {
        when:
        resumeCommand(endedExperiment().id).execute()

        then:
        thrown ExperimentCommandException
    }
}
