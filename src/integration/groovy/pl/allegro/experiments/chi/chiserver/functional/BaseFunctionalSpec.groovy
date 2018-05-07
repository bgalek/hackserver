package pl.allegro.experiments.chi.chiserver.functional

import geb.spock.GebSpec
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.AppRunner

@SpringBootTest(
        classes = [
                AppRunner
        ],
        properties = "application.environment=integration",
        webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
abstract class BaseFunctionalSpec extends GebSpec {

    def setup() {
        getBrowser().setBaseUrl(localUrl(""))
    }

    @Value('${local.server.port}')
    protected int port

    protected String localUrl(String endpoint) {
        return "http://localhost:$port$endpoint"
    }
}
