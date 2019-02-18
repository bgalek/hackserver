package pl.allegro.tech.leaders.hackathon.registration

import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import pl.allegro.tech.leaders.hackathon.base.IntegrationSpec
import reactor.core.publisher.Mono

import java.nio.charset.StandardCharsets

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

    def 'should add a team to the team registry'() {
        given:
            String teamName = randomUUID().toString()
        when:
            WebTestClient.ResponseSpec registerResponse = registerTeam(teamName)
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

    def 'should be able to unregister team using secret'() {
        given:
            String teamName = randomUUID().toString()
        when:
            WebTestClient.ResponseSpec registerResponse = registerTeam(teamName)
        then:
            registerResponse
                    .expectStatus()
                    .isCreated()
                    .expectHeader()
                    .valueEquals('Location', "/teams/$teamName")
            String secret = new String(registerResponse.expectBody().returnResult().getResponseBodyContent(), StandardCharsets.UTF_8)
        when:
            WebTestClient.ResponseSpec unregisterResponse = unregisterTeam(teamName, secret)
        then:
            unregisterResponse.expectStatus().is2xxSuccessful()
    }

    def 'should not be able to unregister team without secret'() {
        given:
            String teamName = randomUUID().toString()
        when:
            WebTestClient.ResponseSpec registerResponse = registerTeam(teamName)
        then:
            registerResponse
                    .expectStatus()
                    .isCreated()
                    .expectHeader()
                    .valueEquals('Location', "/teams/$teamName")
            String secret = 'random secret'
        when:
            WebTestClient.ResponseSpec unregisterResponse = unregisterTeam(teamName, secret)
        then:
            unregisterResponse.expectStatus().is4xxClientError()
    }

    def 'should not add the same team twice to the team registry'() {
        given:
            String teamName = randomUUID().toString()
        when:
            WebTestClient.ResponseSpec registerResponse = registerTeam(teamName)
        then:
            registerResponse
                    .expectStatus()
                    .isCreated()
                    .expectHeader()
                    .valueEquals('Location', "/teams/$teamName")
                    .expectBody()
                    .returnResult()
        when:
            WebTestClient.ResponseSpec registerResponse2 = registerTeam(teamName)
        then:
            registerResponse2
                    .expectStatus()
                    .is4xxClientError()
    }

    @LocalServerPort int port

    private WebTestClient.ResponseSpec registerTeam(String teamName) {
        webClient
                .post()
                .uri('/registration')
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just("""{ "name": "$teamName"}""".toString()), String.class)
                .exchange()
    }

    private WebTestClient.ResponseSpec unregisterTeam(String teamName, String secret) {
        webClient
                .delete()
                .uri("/registration/$teamName")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $secret")
                .exchange()
    }
}

