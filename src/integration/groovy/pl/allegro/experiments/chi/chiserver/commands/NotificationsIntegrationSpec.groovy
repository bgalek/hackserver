package pl.allegro.experiments.chi.chiserver.commands

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications.Notification
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications.Notificator

import static pl.allegro.experiments.chi.chiserver.utils.SampleExperimentRequests.sampleExperimentCreationRequest

@ContextConfiguration(classes = [NotificationsIntegrationTestConfig])
class NotificationsIntegrationSpec extends BaseCommandIntegrationSpec {

    @Autowired
    Notificator notificator

    def "should send notification after creating experiment"() {
        given:
        def experimentId = UUID.randomUUID().toString()

        when:
        createExperiment(sampleExperimentCreationRequest([id: experimentId]))

        then:
        with(notificator.sent[0]) {
            expId == experimentId
            actionMessage == "draft was created"
            author == "Root"
        }
    }
}

@Configuration
class NotificationsIntegrationTestConfig {
    @Primary
    @Bean
    Notificator notificator() {
        return new DummyNotificator()
    }
}

class DummyNotificator implements Notificator {
    List<Notification> sent = []

    @Override
    void send(Notification message) {
        sent << message
    }
}