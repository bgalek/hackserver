package pl.allegro.experiments.chi.chiserver.commands

import org.springframework.beans.factory.annotation.Autowired
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.audit.Auditor
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.audit.CommitDetails

class AuditorIntegrationSpec extends BaseCommandIntegrationSpec {

    @Autowired
    Auditor auditor

    def "should track experiment changes"() {
        given:
        def user1 = new User("user1", [], true)
        def user2 = new User("user2", [], true)
        def user3 = new User("user3", [], true)


        when:
        signInAs(user1)
        def experiment = draftExperiment()

        then:
        with(lastChange(experiment.id)) {
            author == "user1"
            date
            changelog.contains("changed on 'id'")
            changelog.contains("changed on 'status'")
            changelog.contains("changed on 'author'")
            changelog.contains("new object: Experiment/${experiment.id}")
        }

        when:
        signInAs(user2)
        startExperiment(experiment.id, 1)

        then:
        with(lastChange(experiment.id)) {
            author == "user2"
            date
            changelog.contains("changed on 'status'")
            changelog.contains("changed on 'activeTo'")
            changelog.contains("changed on 'activeFrom'")
        }

        when:
        signInAs(user3)
        experimentActions.delete(experiment.id)

        then:
        with(lastChange(experiment.id)) {
            author == "user3"
            date
            changelog.contains("object removed: 'Experiment/$experiment.id")
        }
    }

    CommitDetails lastChange(String experimentId) {
        auditor.getAuditLog(experimentId).changes.first()
    }
}
