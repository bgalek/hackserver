package pl.allegro.experiments.chi.chiserver.domain.experiments.administration

import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications.Notificator

class NotificationIntegrationSpec extends BaseIntegrationSpec {

    def "should send email notification after creating experiment"() {
        given:
        def experiments = Mock(Notificator)
        def start = Mock(StartExperimentCommand)
        def commandFactory = Mock(CommandFactory) {
            startExperimentCommand(_, _) >> start
        }

        def notificationFactory = new ExperimentsNotifications(experiments, commandFactory)
        def experimentActions = new ExperimentActions(notificationFactory)

        def startExperimentProperties = new StartExperimentProperties(1)
        when:
        experimentActions.start('222', startExperimentProperties)

        then:
        1 * experiments.send('Experiment notification', _)
        1 * start.execute() >> null
    }
}
