package pl.allegro.tech.leaders.hackathon.base

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient
import pl.allegro.tech.leaders.hackathon.challenge.infrastucture.InitialChallengeDefinitionRegistrar
import spock.lang.Specification

@CompileStatic
@Import(TestMongoConfig1)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "client.timeout=300")
abstract class IntegrationSpec extends Specification implements DatabaseCleaner {

    @Autowired
    InitialChallengeDefinitionRegistrar initialChallengeDefinitionRegistrar
    @Autowired
    WebTestClient webClient

    void setup() {
        initialChallengeDefinitionRegistrar.registerChallengeDefinitions()
    }

    void cleanup() {
        dropAllCollections()
    }
}
