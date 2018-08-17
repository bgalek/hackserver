package pl.allegro.experiments.chi.chiserver.interactions

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications.OAuthTokenResponse
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications.TokenRetriever

@Configuration
class EmailNotificationConfig {

    @Primary
    @Bean
    FakeTokenRetiriever fakeTokenRetriever() {
        return new FakeTokenRetiriever()

    }

    class FakeTokenRetiriever implements TokenRetriever {

        @Override
        OAuthTokenResponse getOAuthTokenInformation() {
            return null
        }
    }}