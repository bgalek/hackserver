
package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.AuthorizationException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create.CreateExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create.ExperimentCreationException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create.ExperimentCreationRequest
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.InMemoryExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsTestConfig
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.FileBasedExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MutableUserProvider

import static pl.allegro.experiments.chi.chiserver.application.administration.CommandTestUtils.simpleExperimentRequest

@ContextConfiguration(classes = [ExperimentsTestConfig])
@DirtiesContext
class CreateExperimentCommandIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    ExperimentsRepository experimentsRepository

    @Autowired
    FileBasedExperimentsRepository fileBasedExperimentsRepository

    @Autowired
    InMemoryExperimentsRepository inMemoryExperimentsRepository

    @Autowired
    MutableUserProvider mutableUserProvider


    def "should create experiment"() {
        given:
        def id = "simpleId"
        mutableUserProvider.user = new User('Root', [], true)

        def command = new CreateExperimentCommand(experimentsRepository, mutableUserProvider, simpleExperimentRequest(id))

        when:
        command.execute()

        then:
        experimentsRepository.getExperiment("simpleId").get().id == "simpleId"
    }

    def "should not create experiment where experiment with given id exists"() {
        given:
        def id = "testExperiment"
        mutableUserProvider.user = new User('Root', [], true)

        def command = new CreateExperimentCommand(inMemoryExperimentsRepository, mutableUserProvider, simpleExperimentRequest(id))

        when:
        command.execute()

        then:
        thrown(ExperimentCreationException)
    }

    def "should not create experiment when user is anonymous"() {
        given:
        def id = "simpleId"
        mutableUserProvider.user = new User(User.ANONYMOUS, [], false)

        def command = new CreateExperimentCommand(experimentsRepository, mutableUserProvider, simpleExperimentRequest(id))

        when:
        command.execute()

        then:
        thrown(AuthorizationException)
    }

    def "should not create experiment when request cannot be converted to object"() {
        given:
        mutableUserProvider.user = new User('root', [], true)

        def brokenRequest = new ExperimentCreationRequest(
                'x',
                ['v1'],
                'xyz',
                1000,
                'a',
                '',
                '',
                [],
                false)

        def command = new CreateExperimentCommand(experimentsRepository, mutableUserProvider, brokenRequest)

        when:
        command.execute()

        then:
        thrown(ExperimentCreationException)
    }
}
