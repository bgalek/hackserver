package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class HttpContentLoader {
    private static final Logger logger = LoggerFactory.getLogger(HttpContentLoader.class);

    private final RestTemplate restTemplate;

    public HttpContentLoader(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String loadFromHttp(String jsonUrl) {
        logger.info("loading data from URL: " + jsonUrl);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity<String>(headers);
        return restTemplate.exchange(jsonUrl, HttpMethod.GET, entity, String.class).getBody();
    }
}
