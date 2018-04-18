package pl.allegro.experiments.chi.chiserver

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@SpringBootTest(
        classes = [
                AppRunner
        ],
        properties = "application.environment=integration",
        webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
abstract class BaseIntegrationSpec extends Specification {

    @Value('${local.server.port}')
    protected int port

    protected String localUrl(String endpoint) {
        return "http://localhost:$port$endpoint"
    }

    HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8)
        headers
    }

    HttpEntity httpJsonEntity(String jsonBody) {
        new HttpEntity<String>(jsonBody, headers())
    }
}
