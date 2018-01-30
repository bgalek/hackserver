package pl.allegro.experiments.chi.chiserver.application.experiments.administration

import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import spock.lang.Specification

class CreateExperimentCommandSpec extends Specification {

    def "should create experiment"() {
        given:
        def id = "simpleId"
        def repository = Stub(ExperimentsRepository) {
            getExperiment(id) >> { return null }
        }
        def userProvider = Stub(UserProvider) {
            getCurrentUser() >> { new User("user1", ["g1"], true) }
        }
        def command = new CreateExperimentCommand(repository, userProvider, simpleExperimentRequest(id))

        when:
        command.execute()

        then:
        notThrown(Exception)
    }

    def "should not create experiment where experiment with given id exists"() {
        given:
        def id = "simpleId"
        def repository = Stub(ExperimentsRepository) {
            getExperiment(id) >> { return Mock(Experiment) }
        }
        def userProvider = Stub(UserProvider) {
            getCurrentUser() >> { new User("user1", ["g1"], true) }
        }
        def command = new CreateExperimentCommand(repository, userProvider, simpleExperimentRequest(id))

        when:
        command.execute()

        then:
        def ex = thrown(ExperimentCreationException)
        ex.message == 'Experiment with id simpleId already exists'
    }


    def "should not create experiment when user is not a root"() {
        given:
        def id = "simpleId"
        def repository = Stub(ExperimentsRepository) {
            getExperiment(id) >> { return Mock(Experiment) }
        }
        def userProvider = Stub(UserProvider) {
            getCurrentUser() >> { new User("user1", ["g1"], false) }
        }
        def command = new CreateExperimentCommand(repository, userProvider, simpleExperimentRequest(id))

        when:
        command.execute()

        then:
        def ex = thrown(AuthorizationException)
        ex.message == 'User user1 cannot create experiments'
    }

    def simpleExperimentRequest(String id) {
        def variants = [new ExperimentCreationRequest.Variant("v1", [new ExperimentCreationRequest.Predicate(ExperimentCreationRequest.PredicateType.INTERNAL, null, null, null)])]
        return new ExperimentCreationRequest(id, variants, "simple description", ["group a", "group b"], true)
    }

}
