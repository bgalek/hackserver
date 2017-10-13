package pl.allegro.experiments.chi.chiserver

import com.github.tomakehurst.wiremock.WireMockServer
import groovy.transform.TypeChecked
import org.junit.After
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@SpringBootTest(
        classes = [
                AppRunner,
                HermesTestConfig,
                WireMockTestConfig
        ],
        properties = "application.environment=integration",
        webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
@TypeChecked
abstract class BaseIntegrationSpec extends Specification {
    @Autowired
    WireMockServer wireMockServer

    @Value('${local.server.port}')
    protected int port

    @After
    void resetWireMockStubs() {
        if (wireMockServer != null) {
            wireMockServer.resetMappings()
        }
    }

    protected String localUrl(String endpoint) {
        return "http://localhost:$port$endpoint"
    }
}
