package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class NotificationConfig {

    @Bean
    Notificator notificator(RestTemplate restTemplate,
                            @Value("${notificator.enabled}") boolean notificationsEnabled,
                            @Value("${notificator.hipChatPostUrl}") String hipChatPostUrl
                            ) {
        if (notificationsEnabled) {
            return new HipChatNotificator(restTemplate, hipChatPostUrl);
        }
        return (m) -> {};
    }
}
