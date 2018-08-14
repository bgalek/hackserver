package pl.allegro.experiments.chi.chiserver.domain.experiments.administration

import org.springframework.beans.factory.annotation.Autowired
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.application.administration.CommandTestUtils
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications.EmailService
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications.Notificator
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository

class ExperimentActionsIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    UserProvider userProvider

    def "should send email notification after creating experiment"() {
        given:
        def experiments = Mock(Notificator)

        def start = new BasicExperimentCommand(Mock(StartExperimentCommand), experiments, userProvider)
        def commandFactory = Mock(CommandFactory) {
            startExperimentCommand(_, _) >> start
        }

        def experimentActions = new ExperimentActions(commandFactory)

        def startExperimentProperties = new StartExperimentProperties(1)
        when:
        experimentActions.start('222', startExperimentProperties)

        then:
        1 * experiments.send('Experiment notification', _)
    }

}
