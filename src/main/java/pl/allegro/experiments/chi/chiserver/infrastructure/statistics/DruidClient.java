package pl.allegro.experiments.chi.chiserver.infrastructure.statistics;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DruidClient {
    private final String druidApiHost;
    private final RestTemplate restTemplate;

    public DruidClient(String druidApiHost, RestTemplate restTemplate) {
        this.druidApiHost = druidApiHost;
        this.restTemplate = restTemplate;
    }

    public String query(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> query = new HttpEntity<>(body, headers);
        try {
            return restTemplate.postForEntity(
                    "http://" + druidApiHost + "/druid/v2/?pretty",
                    query, String.class
            ).getBody();
        } catch (Exception e) {
            throw new DruidException("Error while trying to get data from " + druidApiHost, e);
        }
    }

    public static String oneDayIntervals(LocalDate date) {
        String dateStr = DateTimeFormatter.ISO_LOCAL_DATE.format(date);
        return dateStr + "T00Z/" + dateStr + "T23:59:59.999Z";
    }

    public static String lastDayIntervals() {
        return oneDayIntervals(LocalDate.now().minusDays(1));
    }
}
