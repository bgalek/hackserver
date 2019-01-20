package pl.allegro.tech.leaders.hackathon

import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import pl.allegro.tech.leaders.hackathon.base.IntegrationSpec
import reactor.core.publisher.Mono

import static java.util.UUID.randomUUID

class RegistrationSpec extends IntegrationSpec {

    def 'empty team registry should serve no teams'() {
        expect:
            webClient.get()
                .uri('/registration')
                .exchange()
                .expectBody()
                .jsonPath('$')
                .isEmpty()
    }

    def 'should add a team to a team registry'() {
        given:
            String teamName = randomUUID().toString()
            String registerTeamBody = """{ "name": "$teamName"}"""
        when:
            WebTestClient.ResponseSpec registerResponse = webClient
                .post()
                .uri('/registration')
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(registerTeamBody), String)
                .exchange()
        then:
            registerResponse
                .expectStatus()
                .isCreated()
                .expectHeader()
                .valueEquals('Location', "/teams/$teamName")
        when:
            WebTestClient.ResponseSpec fetchResponse = webClient
                .get()
                .uri('/registration')
                .exchange()
        then:
            fetchResponse.expectBody()
                .jsonPath('$[0].name')
                .isEqualTo(teamName)
    }
}

