package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentActions
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.audit.Auditor
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.StartExperimentProperties
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MutableUserProvider

import static pl.allegro.experiments.chi.chiserver.application.administration.CommandTestUtils.simpleExperimentRequest

@DirtiesContext
class AuditorIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    MutableUserProvider mutableUserProvider

    @Autowired
    Auditor auditor

    @Autowired
    ExperimentActions experimentActions

    def "should track experiment changes"() {
        when:
        def id = "sample-experiment-id"
        signInAs("user1")
        experimentActions.create(simpleExperimentRequest(id))
        def auditLog = auditor.getAuditLog(id)

        then:
        auditLog.experimentId == id
        with(auditLog.changes.first()) {
            author == "user1"
            date
            changelog.contains("changed on 'id'")
            changelog.contains("changed on 'description'")
            changelog.contains("changed on 'documentLink'")
            changelog.contains("changed on 'groups'")
            changelog.contains("changed on 'status'")
            changelog.contains("changed on 'author'")
            changelog.contains("new object: Experiment/$id")
        }

        when:
        signInAs("user2")
        experimentActions.start(id, new StartExperimentProperties(1))

        then:
        with(lastChange(id)) {
            author == "user2"
            date
            changelog.contains("changed on 'status'")
            changelog.contains("changed on 'activeTo'")
            changelog.contains("changed on 'activeFrom'")
        }

        when:
        signInAs("user3")
        experimentActions.delete(id)

        then:
        with(lastChange(id)) {
            author == "user3"
            date
            changelog.contains("object removed: 'Experiment/$id")
        }
    }

    def lastChange(String id) {
        def auditLog = auditor.getAuditLog(id)
        return auditLog.changes.first()
    }

    def signInAs(String username) {
        mutableUserProvider.user = new User(username, [], true)
    }
}
