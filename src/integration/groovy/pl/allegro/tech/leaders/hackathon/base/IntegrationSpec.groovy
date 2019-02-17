package pl.allegro.tech.leaders.hackathon.base

import groovy.transform.CompileStatic
import org.junit.After
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import pl.allegro.tech.leaders.hackathon.challenge.infrastucture.InitialChallengeDefinitionRegistrar
import spock.lang.Specification

@CompileStatic
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                properties = "client.timeout=100")
abstract class IntegrationSpec extends Specification implements DatabaseCleaner {

    @Autowired InitialChallengeDefinitionRegistrar initialChallengeDefinitionRegistrar
    @Autowired WebTestClient webClient

    @Before
    void registerChallengeDefinitions() {
        initialChallengeDefinitionRegistrar.registerChallengeDefinitions()
    }

    @After
    void cleanUpDb() {
        dropAllCollections()
    }
}
