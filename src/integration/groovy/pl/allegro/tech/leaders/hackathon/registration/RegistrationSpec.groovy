package pl.allegro.tech.leaders.hackathon.registration

import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import pl.allegro.tech.leaders.hackathon.base.IntegrationSpec
import pl.allegro.tech.leaders.hackathon.registration.infrastructure.RegistrationEventPublisher
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

    def 'should add a team to a team registry'() {
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

    def 'should send an event through websocket session on team registration'() {
        given: 'websocket client'
            ReactorNettyWebSocketClient socketClient = new ReactorNettyWebSocketClient()
            WebSocketHandler sessionHandler = new LoggingMessagesSessionHandler()
        when: 'socket client connects'
            socketClient.execute(URI.create("ws://localhost:$port/ws/registrations"), sessionHandler).subscribe()
        then: 'nothing really happens'
            sessionHandler.messages.size() == 0
        when: 'when a new team is registered (10 times)'
            10.times { registerTeam('some-name-' + it) }
        then: 'client should receive 10 messages'
            sessionHandler.messages.size() == 10
            sessionHandler.messages.eachWithIndex { it, index ->
                jsonSlurper.parseText(it).name == 'some-name-' + index
            }
    }

    @LocalServerPort int port
    @Autowired RegistrationEventPublisher registrationEventPublisher
    JsonSlurper jsonSlurper = new JsonSlurper()

    def cleanup() {
        registrationEventPublisher.queue.clear()
    }

    private WebTestClient.ResponseSpec registerTeam(String teamName) {
        webClient
                .post()
                .uri('/registration')
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just("""{ "name": "$teamName"}""".toString()), String.class)
                .exchange()
    }

    class LoggingMessagesSessionHandler implements WebSocketHandler {
        private List<String> messages = []

        Mono<Void> handle(WebSocketSession webSocketSession) {
            return webSocketSession
                    .receive()
                    .doOnNext({ messages.add(it.getPayloadAsText(StandardCharsets.UTF_8)) })
                    .then()
        }

        List<String> getMessages() {
            return messages
        }
    }
}

