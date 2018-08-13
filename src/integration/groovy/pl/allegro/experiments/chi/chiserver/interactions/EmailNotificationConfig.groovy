package pl.allegro.experiments.chi.chiserver.interactions

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications.Notificator
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications.OAuthTokenResponse
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications.TokenRetriever

@Configuration
class EmailNotificationConfig {

    @Primary
    @Bean
    FakeNotificator emailService() {
        return new FakeNotificator()
    }

    @Primary
    @Bean
    FakeTokenRetiriever fakeTokenRetriever() {
        return new FakeTokenRetiriever()

    }

    class FakeNotificator implements Notificator {

        @Override
        void send(String subject, String message) {
        }
    }

    class FakeTokenRetiriever implements TokenRetriever {

        @Override
        OAuthTokenResponse getOAuthTokenInformation() {
            return null
        }
    }}