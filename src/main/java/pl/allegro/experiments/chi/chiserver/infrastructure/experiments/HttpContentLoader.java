package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

class HttpContentLoader implements DataLoader {
    private static final Logger logger = LoggerFactory.getLogger(HttpContentLoader.class);

    private final RestTemplate restTemplate;

    HttpContentLoader(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String load(String jsonUrl) {
        logger.info("loading data from URL: " + jsonUrl);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity<String>(headers);
        return restTemplate.exchange(jsonUrl, HttpMethod.GET, entity, String.class).getBody();
    }
}
