package pl.allegro.experiments.chi.chiserver.functional

import geb.spock.GebSpec
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.AppRunner
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider

@SpringBootTest(
        classes = [
                AppRunner
        ],
        properties = "application.environment=integration",
        webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
abstract class BaseFunctionalSpec extends GebSpec {

    @Value('${local.server.port}')
    protected int port

    def setup() {
        getBrowser().setBaseUrl("http://localhost:$port")
    }

}

@Configuration
class LoggedInAsRootTestConfig {

    @Primary
    @Bean
    UserProvider mutableUserProvider() {
        return new UserProvider() {
            @Override
            User getCurrentUser() {
                return new User("Anonymous", [], true)
            }
        }
    }
}