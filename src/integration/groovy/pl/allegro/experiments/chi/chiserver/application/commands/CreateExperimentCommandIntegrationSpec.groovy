package pl.allegro.experiments.chi.chiserver.application.commands

import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.AuthorizationException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCreationRequest

import static pl.allegro.experiments.chi.chiserver.utils.SampleExperimentRequests.sampleExperimentCreationRequest

class CreateExperimentCommandIntegrationSpec extends BaseCommandIntegrationSpec {

    def "should create experiment"() {
        given:
        def experimentId = "simple id"

        when:
        createExperiment(sampleExperimentCreationRequest([id: experimentId]))

        then:
        experimentsExists(experimentId)
        fetchExperiment(experimentId).id == "simple id"
    }

    def "should not create experiment where experiment with given id exists"() {
        given:
        def experiment = draftExperiment()

        when:
        createExperiment(sampleExperimentCreationRequest([id: experiment.id]))

        then:
        def exception = thrown ExperimentCommandException
        exception.message == "Experiment with id ${experiment.id} already exists"
    }

    def "should not create experiment when user is anonymous"() {
        given:
        def anonymousUser = new User(User.ANONYMOUS, [], false)

        when:
        signInAs(anonymousUser)
        createExperiment(sampleExperimentCreationRequest())

        then:
        thrown AuthorizationException
    }

    def "should not create experiment when request cannot be converted to object"() {
        given:
        def experimentId = 'x'
        def brokenRequest = ExperimentCreationRequest.builder()
                .id(experimentId)
                .variantNames(['v1'])
                .internalVariantName('xyz')
                .percentage(1000)
                .deviceClass('a')
                .description('')
                .documentLink('')
                .groups([])
                .build()

        when:
        createExperiment(brokenRequest)

        then:
        def exception = thrown ExperimentCommandException
        exception.message == "Cannot create experiment from request"

        and:
        !experimentsExists(experimentId)
    }
}
