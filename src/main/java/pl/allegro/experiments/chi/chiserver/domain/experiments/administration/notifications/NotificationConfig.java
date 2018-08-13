package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications;

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
    EmailService notificator(AuthenticationClient authenticationClient, EmailNotifierProperties emailNotifierProperties, RestTemplate restTemplate) {
        return new EmailService(authenticationClient, emailNotifierProperties, restTemplate);
    }

    @Bean
    AuthenticationClient authenticationClient(OAuthProperties oAuthProperties, RestTemplate restTemplate) {
        return new AuthenticationClient(oAuthProperties, restTemplate);
    }
}
