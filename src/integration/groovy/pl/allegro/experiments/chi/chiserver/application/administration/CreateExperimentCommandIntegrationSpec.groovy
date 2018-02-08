package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.application.experiments.administration.AuthorizationException
import pl.allegro.experiments.chi.chiserver.application.experiments.administration.CreateExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCreationException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCreationRequest
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
        experimentsRepository.getExperiment("simpleId").id == "simpleId"
    }

    def "should not create experiment where experiment with given id exists"() {
        given:
        def id = "testExperiment"
        mutableUserProvider.user = new User('Root', [], true)

        def command = new CreateExperimentCommand(inMemoryExperimentsRepository, mutableUserProvider, simpleExperimentRequest(id))

        when:
        command.execute()

        then:
        def ex = thrown(ExperimentCreationException)
        ex.message == 'Experiment with id testExperiment already exists'
    }

    def "should not create experiment when user is not a root"() {
        given:
        def id = "simpleId"
        mutableUserProvider.user = new User('user1', [], false)

        def command = new CreateExperimentCommand(experimentsRepository, mutableUserProvider, simpleExperimentRequest(id))

        when:
        command.execute()

        then:
        def ex = thrown(AuthorizationException)
        ex.message == 'User user1 cannot create experiments'
    }

    def "should not create experiment when request cannot be converted to object"() {
        given:
        mutableUserProvider.user = new User('root', [], true)

        def brokenRequest = new ExperimentCreationRequest('x', [new ExperimentCreationRequest.Variant('v1', [new ExperimentCreationRequest.Predicate(ExperimentCreationRequest.PredicateType.HASH, null, null, null, null)])], '', [], false)

        def command = new CreateExperimentCommand(experimentsRepository, mutableUserProvider, brokenRequest)

        when:
        command.execute()

        then:
        def ex = thrown(ExperimentCreationException)
        ex.message == 'Cannot create experiment from request ExperimentCreationRequest(id=x, variants=[Variant(name=v1, predicates=[Predicate(type=HASH, from=null, to=null, regexp=null, device=null)])], description=, groups=[], reportingEnabled=false)'
    }
}
