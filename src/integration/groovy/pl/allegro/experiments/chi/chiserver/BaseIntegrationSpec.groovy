package pl.allegro.experiments.chi.chiserver

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.kafka.test.rule.KafkaEmbedded
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

@SpringBootTest(
        classes = [
                AppRunner
        ],
        properties = "application.environment=integration",
        webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class BaseIntegrationSpec extends Specification {

    @Value('${local.server.port}')
    protected int port

    RestTemplate restTemplate = new RestTemplate()

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

    def createDraftExperiment(int percentage=10) {
        String experimentId = UUID.randomUUID().toString()
        def request = [
                id                 : experimentId,
                description        : 'desc',
                documentLink       : 'https://vuetifyjs.com/vuetify/quick-start',
                variantNames       : ['base', 'v3'],
                internalVariantName: 'v3',
                percentage         : percentage,
                deviceClass        : 'phone',
                groups             : ['group a', 'group b'],
                reportingEnabled   : true,
                reportingType: 'BACKEND'
        ]
        restTemplate.postForEntity(localUrl('/api/admin/experiments'), request, Map)
        experimentId
    }

    def startExperiment(String experimentId) {
        def startRequest = [
                experimentDurationDays: 30
        ]
        restTemplate.put(localUrl("/api/admin/experiments/${experimentId}/start"), startRequest, Map)
    }
}
