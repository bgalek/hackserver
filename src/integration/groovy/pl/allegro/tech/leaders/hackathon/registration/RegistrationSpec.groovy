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
            int port = 8181
        when:
            WebTestClient.ResponseSpec registerResponse = registerTeam(teamName, port)
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
                    .jsonPath('$[0].port')
                    .isEqualTo(port)
    }

    def 'should be able to update team using secret'() {
        given:
            String teamName = randomUUID().toString()
            int port = 8181
            WebTestClient.ResponseSpec registerResponse = registerTeam(teamName, port)
            String secret = bodyAsString(registerResponse)
        when:
            WebTestClient.ResponseSpec updateResponse = updateTeam(teamName, port, secret)
        then:
            updateResponse.expectStatus().is2xxSuccessful()
    }

    def 'should not be able to update team without secret'() {
        given:
            String teamName = randomUUID().toString()
            int port = 8181
            registerTeam(teamName, port)
            String secret = 'not real secret'
        when:
            WebTestClient.ResponseSpec updateResponse = updateTeam(teamName, port, secret)
        then:
            updateResponse.expectStatus().is4xxClientError()
    }

    def 'should not add the same team twice to the team registry'() {
        given:
            String teamName = randomUUID().toString()
            int port = 8181
            registerTeam(teamName, port)
        when:
            WebTestClient.ResponseSpec registerResponse = registerTeam(teamName, port)
        then:
            registerResponse
                    .expectStatus()
                    .is4xxClientError()
    }

    @LocalServerPort int port

    private static String bodyAsString(WebTestClient.ResponseSpec responseSpec) {
        new String(responseSpec.expectBody().returnResult().getResponseBodyContent(), StandardCharsets.UTF_8)
    }

    private WebTestClient.ResponseSpec registerTeam(String teamName, int port) {
        webClient
                .post()
                .uri('/registration')
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just("""{ "name": "$teamName", "port": ${port}}""".toString()), String.class)
                .exchange()
    }

    private WebTestClient.ResponseSpec updateTeam(String teamName, int port, String secret) {
        webClient
                .patch()
                .uri("/registration/$teamName")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $secret")
                .body(Mono.just("""{ "port": ${port} }""".toString()), String.class)
                .exchange()
    }
}

