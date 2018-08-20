package pl.allegro.experiments.chi.chiserver.commands

import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.AuthorizationException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentNotFoundException
import spock.lang.Unroll

class CommandValidationIntegrationSpec extends BaseCommandIntegrationSpec {

    @Unroll
    def "should execute command when user has permissions"() {
        given:
        signInAs(ROOT_USER)
        def experiment = draftExperiment([groups: ["group a", "group b"]])

        and:
        signInAs(user)

        when:
        startExperiment(experiment.id, 14)

        then:
        notThrown AuthorizationException
        fetchExperiment(experiment.id).isActive()

        when:
        def activeTo = fetchExperiment(experiment.id).activeTo
        prolongExperiment(experiment.id, 30)

        then:
        notThrown AuthorizationException
        fetchExperiment(experiment.id).activeTo == activeTo.plusDays(30)

        when:
        stopExperiment(experiment.id)

        then:
        notThrown AuthorizationException
        fetchExperiment(experiment.id).isEnded()

        where:
        user << [
                new User('Root', [], false), // owner
                new User('Normal', ['group a'], false),
                new User('Other Root', [], true),
                new User('Normal', ['nonexistent', 'group a'], false),
                new User('Root with group', ['group a'], true)
        ]
    }

    @Unroll
    def "should not execute command when user has no permissions"() {
        when:
        signInAs(ROOT_USER)
        def experiment = draftExperiment([groups: ["group a", "group b"]])
        signInAs(user)
        startExperiment(experiment.id, 14)

        then:
        thrown AuthorizationException

        when:
        signInAs(ROOT_USER)
        experiment = startedExperiment([groups: ["group a", "group b"]])
        signInAs(user)
        prolongExperiment(experiment.id, 30)

        then:
        thrown AuthorizationException

        when:
        signInAs(ROOT_USER)
        experiment = startedExperiment([groups: ["group a", "group b"]])
        signInAs(user)
        stopExperiment(experiment.id)

        then:
        thrown AuthorizationException

        where:
        user << [
                new User('Other', [], false),
                new User('Other', ['unknown group'], false)
        ]
    }

    def "should not execute command when experiment does not exist"() {
        given:
        def nonexistentId = 'nonexistent id'

        when:
        startExperiment(nonexistentId, 14)

        then:
        thrown ExperimentNotFoundException

        when:
        prolongExperiment(nonexistentId, 30)

        then:
        thrown ExperimentNotFoundException

        when:
        stopExperiment(nonexistentId)

        then:
        thrown ExperimentNotFoundException
    }
}