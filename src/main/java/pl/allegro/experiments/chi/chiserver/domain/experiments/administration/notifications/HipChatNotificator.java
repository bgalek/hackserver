package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HipChatNotificator implements Notificator {
    private static final Logger logger = LoggerFactory.getLogger(HipChatNotificator.class);

    private final RestTemplate restTemplate;
    private final String hipChatPostUrl;
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    HipChatNotificator(RestTemplate restTemplate, String hipChatPostUrl) {
        this.restTemplate = restTemplate;
        this.hipChatPostUrl = hipChatPostUrl;
    }

    public void send(Notification message) {
        try {
            var notification = new HipChatNotification(message);
            var headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            var entity = new HttpEntity<>(notification, headers);

            executor.submit(() -> {
                ResponseEntity<String> response = restTemplate.postForEntity(hipChatPostUrl, entity, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    logger.info("HipChat notification '{}' sent", notification);
                } else {
                    logger.error("Error while sending HipChat notification - " + response);
                }
            });

        }
        catch (RuntimeException e) {
            logger.error("Error while sending HipChat notification", e);
        }
    }

}
