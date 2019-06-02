package pl.allegro.tech.leaders.hackathon.runner

import groovy.json.JsonSlurper
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder
import pl.allegro.tech.leaders.hackathon.base.IntegrationSpec
import pl.allegro.tech.leaders.hackathon.challenge.samples.CalculatorChallengeDefinition
import reactor.core.publisher.Mono

import java.util.concurrent.TimeUnit

class TaskRunnerSpec extends IntegrationSpec {
    @Autowired
    CalculatorChallengeDefinition calculatorChallengeDefinition
    MockWebServer mockWebServer = new MockWebServer()

    String CHALLENGE_ID
    String CHALLENGE_ENDPOINT
    String TEAM_ID = 'team-a'

    void setup() {
        CHALLENGE_ID = calculatorChallengeDefinition.id
        CHALLENGE_ENDPOINT = UriComponentsBuilder.newInstance()
                .path(calculatorChallengeDefinition.challengeEndpoint)
                .queryParams(calculatorChallengeDefinition.example.parameters)
                .build()
                .toString()
    }

    def "should return 404 when running an example task for an unregistered team"() {
        given:
            activateChallenge(CHALLENGE_ID)

        when:
            def response = runExampleTask(CHALLENGE_ID, TEAM_ID)

        then:
            response.expectStatus().isNotFound()
    }

    def "should return 404 when running an example task on inactive challenge"() {
        given:
            registerTeam(TEAM_ID)

        when:
            def response = runExampleTask(CHALLENGE_ID, TEAM_ID)

        then:
            response.expectStatus().isNotFound()
    }

    def "should give max score when a solution of an example task is right"() {
        given:
            activateChallenge(CHALLENGE_ID)
            registerTeam(TEAM_ID)
            stubTeamResponse(CHALLENGE_ENDPOINT, "4")

        when:
            def response = runExampleTask(CHALLENGE_ID, TEAM_ID)

        then:
            response.expectStatus().is2xxSuccessful()
            def result = new JsonSlurper().parse(response.expectBody().returnResult().getResponseBody())

            println result

            result.score == 4
            result.responseHttpStatus == 200
            result.responseBody == "4"
            !result.errorMessage
    }

    def "should give 0 score when a solution of an example task is wrong"() {
        given:
            activateChallenge(CHALLENGE_ID)
            registerTeam(TEAM_ID)
            stubTeamResponse(CHALLENGE_ENDPOINT, "22")

        when:
            def response = runExampleTask(CHALLENGE_ID, TEAM_ID)

        then:
            response.expectStatus().is2xxSuccessful()
            def result = new JsonSlurper().parse(response.expectBody().returnResult().getResponseBody())

            println result

            result.score == 0
            result.responseHttpStatus == 200
            result.responseBody == "22"
            !result.errorMessage
    }

    def "should give 0 score when a client's response isn't 2xx"() {
        given:
            activateChallenge(CHALLENGE_ID)
            registerTeam(TEAM_ID)
            stubTeamResponse(CHALLENGE_ENDPOINT, "4", 503)

        when:
            def response = runExampleTask(CHALLENGE_ID, TEAM_ID)

        then:
            response.expectStatus().is2xxSuccessful()
            def result = new JsonSlurper().parse(response.expectBody().returnResult().getResponseBody())

            println result

            result.score == 0
            result.responseHttpStatus == 503
            result.responseBody == "4"
            result.errorMessage
    }

    def "should give 0 score when a client's connection got timeouted"() {
        given:
            activateChallenge(CHALLENGE_ID)
            registerTeam(TEAM_ID)
            stubTeamResponse(CHALLENGE_ENDPOINT, "4", 200, 400)

        when:
            def response = runExampleTask(CHALLENGE_ID, TEAM_ID)

        then:
            response.expectStatus().is2xxSuccessful()
            def result = new JsonSlurper().parse(response.expectBody().returnResult().getResponseBody())

            println result

            result.score == 0
            result.responseHttpStatus == 503
            result.latencyMillis >= 300
            result.errorMessage
    }

    def "should remove one point for each 200 millis of latency"() {
        given:
            activateChallenge(CHALLENGE_ID)
            registerTeam(TEAM_ID)
            stubTeamResponse(CHALLENGE_ENDPOINT, "4", 200, 210)

        when:
            def response = runExampleTask(CHALLENGE_ID, TEAM_ID)

        then:
            response.expectStatus().is2xxSuccessful()
            def result = new JsonSlurper().parse(response.expectBody().returnResult().getResponseBody())

            println result

            result.score == 3
            result.responseHttpStatus == 200
            result.responseBody == "4"
            result.latencyMillis >= 200
            !result.errorMessage
    }

    def "should give 0 score when a client's response can't be parsed to a required type"() {
        given:
            activateChallenge(CHALLENGE_ID)
            registerTeam(TEAM_ID)
            stubTeamResponse(CHALLENGE_ENDPOINT, "[4]")

        when:
            def response = runExampleTask(CHALLENGE_ID, TEAM_ID)

        then:
            response.expectStatus().is2xxSuccessful()
            def result = new JsonSlurper().parse(response.expectBody().returnResult().getResponseBody())

            println result

            result.score == 0
            result.responseHttpStatus == 200
            result.responseBody == "[4]"
            result.errorMessage
    }

    private WebTestClient.ResponseSpec runExampleTask(CHALLENGE_ID, TEAM_ID) {
        webClient.get().uri("/challenges/$CHALLENGE_ID/run-example?team-id=$TEAM_ID").exchange()
    }

    private void activateChallenge(String challengeId) {
        webClient.put().uri("/challenges/$challengeId/activate").exchange()
                .expectStatus().is2xxSuccessful()
    }

    private void registerTeam(String teamId, int port = 8080) {
        mockWebServer.start(InetAddress.getByName('127.0.0.1'), port)
        webClient.post()
                .uri('/registration')
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just("""{ "name": "$teamId", "port": ${port}}""".toString()), String)
                .exchange()
                .expectStatus().is2xxSuccessful()
    }

    void stubTeamResponse(String endpoint, String response, int status = 200, int latency = 0) {
        mockWebServer.url(endpoint)
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(status)
                .setBodyDelay(latency, TimeUnit.MILLISECONDS)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(response))
    }

    void cleanup() {
        mockWebServer.shutdown()
    }
}
