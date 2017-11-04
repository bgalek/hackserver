package pl.allegro.experiments.chi.chiserver

import com.github.tomakehurst.wiremock.WireMockServer
import org.junit.After
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.interactions.InteractionsIntegrationTestConfig
import spock.lang.Specification

@SpringBootTest(
        classes = [
                AppRunner,
                InteractionsIntegrationTestConfig
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
}
