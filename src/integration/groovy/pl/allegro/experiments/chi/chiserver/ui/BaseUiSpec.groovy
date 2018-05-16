package pl.allegro.experiments.chi.chiserver.ui

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
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.utils.SampleInMemoryExperimentsRepository

@SpringBootTest(
        classes = [
                AppRunner
        ],
        properties = "application.environment=integration",
        webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
abstract class BaseUiSpec extends GebSpec {

    @Value('${local.server.port}')
    protected int port

    def setup() {
        getBrowser().setBaseUrl("http://localhost:$port")
    }

}

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

    @Primary
    @Bean
    ExperimentsRepository experimentsRepository() {
        return SampleInMemoryExperimentsRepository.createSampleRepository();
    }
}