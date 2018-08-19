package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.CommandFactory
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCreationRequest
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications.Notification
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications.Notificator
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MutableUserProvider

@ContextConfiguration(classes = [NotificationsIntegrationTestConfig])
class NotificationsIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    MutableUserProvider mutableUserProvider

    @Autowired
    Notificator notificator

    @Autowired
    CommandFactory commandFactory

    def "should send notification after creating experiment"() {
        given:
        mutableUserProvider.user = new User('Root', [], true)
        def experimentId = UUID.randomUUID().toString()

        when:
        commandFactory
            .createExperimentCommand(ExperimentCreationRequest.builder()
            .id(experimentId)
            .variantNames(["a","b"])
            .percentage(10)
            .build())
            .execute()

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