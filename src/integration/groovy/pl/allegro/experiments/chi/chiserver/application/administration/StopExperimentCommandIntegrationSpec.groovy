package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.*
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsTestConfig
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MutableUserProvider
import spock.lang.Unroll

import static pl.allegro.experiments.chi.chiserver.application.administration.CommandTestUtils.simpleExperimentRequest
import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.*

@ContextConfiguration(classes = [ExperimentsTestConfig])
class StopExperimentCommandIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    ExperimentsRepository experimentsRepository

    @Autowired
    CommandFactory commandFactory

    @Autowired
    MutableUserProvider mutableUserProvider

    @Unroll
    def "should stop #status experiment"() {
        given:
        def experiment = creator(this)

        when:
        stopCommand(experiment.id).execute()

        then:
        experimentsRepository.getExperiment(experiment.id).get().isEnded()

        where:
        status  | creator
        ACTIVE  | { it.startedExperiment() }
        FULL_ON | { it.fullOnExperiment() }
    }

    def "should not stop nonexistent experiment"() {
        when:
        def nonexistentExperimentId = 'nonexistentExperimentId'
        stopCommand(nonexistentExperimentId).execute()

        then:
        def exception = thrown ExperimentNotFoundException
        exception.message == "Experiment not found: $nonexistentExperimentId"
    }

    @Unroll
    def "should not stop #status experiment"() {
        given:
        def experiment = creator(this)

        when:
        stopCommand(experiment.id).execute()

        then:
        def exception = thrown ExperimentCommandException
        exception.message == "$status experiment cannot be ended"

        where:
        status | creator
        DRAFT  | { it.draftExperiment() }
        ENDED  | { it.endedExperiment() }
    }

    Experiment fullOnExperiment() {
        def experiment = startedExperiment()
        def command = commandFactory.makeExperimentFullOnCommand(
                experiment.id,
                new MakeExperimentFullOnProperties(experiment.variants.first().getName()))
        command.execute()
        experimentsRepository.getExperiment(experiment.id).get()
    }

    Experiment endedExperiment() {
        def experiment = startedExperiment()
        def command = stopCommand(experiment.id)
        command.execute()
        experimentsRepository.getExperiment(experiment.id).get()
    }

    Experiment startedExperiment() {
        def experiment = draftExperiment()
        def command = commandFactory.startExperimentCommand(
                experiment.id,
                new StartExperimentProperties(30))
        command.execute()
        experimentsRepository.getExperiment(experiment.id).get()
    }

    Experiment draftExperiment() {
        def id = UUID.randomUUID().toString()
        mutableUserProvider.user = new User('Root', [], true)
        def command = commandFactory.createExperimentCommand(simpleExperimentRequest(id))
        command.execute()
        return experimentsRepository.getExperiment(id).get()
    }

    StopExperimentCommand stopCommand(String experimentId) {
        return commandFactory.stopExperimentCommand(experimentId)
    }

}
