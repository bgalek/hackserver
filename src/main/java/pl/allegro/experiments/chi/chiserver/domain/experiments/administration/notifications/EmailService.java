package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class EmailService implements Notificator {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final TokenRetriever authenticationClient;
    private final EmailNotifierProperties emailNotifierProperties;
    private final RestTemplate restTemplate;

    public EmailService(TokenRetriever authenticationClient, EmailNotifierProperties emailNotifierProperties, RestTemplate restTemplate) {
        this.authenticationClient = authenticationClient;
        this.emailNotifierProperties = emailNotifierProperties;
        this.restTemplate = restTemplate;
    }

    public void send(String subject, String message) {
        try {
            logger.info("sending notification email '{}' to '{}'", message, emailNotifierProperties.getRecipients());
            restTemplate.postForEntity(emailNotifierProperties.getNotificationUrl(),
                    getEmailNotificationRequest(subject, message, emailNotifierProperties.getRecipients()), String.class);
        }
        catch (RuntimeException e) {
            throw new RuntimeException("Error during sending email notification", e);
        }
    }

    private HttpEntity<Notification> getEmailNotificationRequest(String subject, String message, List<String> recipients) {
        Notification emailNotification = Notification.from(emailNotifierProperties.getCallerId(), subject, message, recipients);
        return new HttpEntity<>(emailNotification, getHttpHeaders());
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        setHeaders(headers);
        return headers;
    }

    private void setHeaders(HttpHeaders headers) {
        OAuthTokenResponse tokenInformation = authenticationClient.getOAuthTokenInformation();
        String authorizationHeader = tokenInformation.getTokenType() + " " + tokenInformation.getAccessToken();
        headers.add(HttpHeaders.AUTHORIZATION, authorizationHeader);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    }
}
