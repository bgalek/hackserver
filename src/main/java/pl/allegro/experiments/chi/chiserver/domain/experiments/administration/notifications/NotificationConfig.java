package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class NotificationConfig {

    @Bean
    @ConfigurationProperties("email-notifier")
    EmailNotifierProperties emailNotifierProperties() {
        return new EmailNotifierProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "oauth")
    OAuthProperties oAuthProperties() {
        return new OAuthProperties();
    }

    @Bean
    Notificator notificator(AuthenticationClient authenticationClient, EmailNotifierProperties emailNotifierProperties, RestTemplate restTemplate,
                             @Value("${email-notifier.enabled:false}") boolean notificationsEnabled) {
        if (notificationsEnabled) {
            return new EmailService(authenticationClient, emailNotifierProperties, restTemplate);
        }
        return (s, m) -> {};
    }

    @Bean
    @ConditionalOnProperty(name = "email-notifier.enabled")
    AuthenticationClient authenticationClient(OAuthProperties oAuthProperties, RestTemplate restTemplate) {
        return new AuthenticationClient(oAuthProperties, restTemplate);
    }
}
