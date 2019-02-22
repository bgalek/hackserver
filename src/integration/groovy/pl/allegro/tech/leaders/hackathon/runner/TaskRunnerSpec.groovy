package pl.allegro.tech.leaders.hackathon.runner

import com.github.tomakehurst.wiremock.junit.WireMockRule
import groovy.json.JsonSlurper
import org.junit.ClassRule
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import pl.allegro.tech.leaders.hackathon.base.IntegrationSpec
import pl.allegro.tech.leaders.hackathon.challenge.samples.CalcChallengeDefinition
import reactor.core.publisher.Mono
import spock.lang.Shared

import static com.github.tomakehurst.wiremock.client.WireMock.*

class TaskRunnerSpec extends IntegrationSpec {
    def CHALLENGE_ID = CalcChallengeDefinition.ID
    def CHALLENGE_ENDPOINT = '/calc?equation=2+2'
    def TEAM_ID = 'team-a'

    @ClassRule
    @Shared
    public WireMockRule wireMock = new WireMockRule(8080)

    def "should return 404 when running an example task for an unregistered team"(){
        given:
        activateChallenge(CHALLENGE_ID)

        when:
        def response = runExampleTask(CHALLENGE_ID, TEAM_ID)

        then:
        response.expectStatus().isNotFound()
    }

    def "should return 404 when running an example task on inactive challenge"(){
        given:
        registerTeam(TEAM_ID)

        when:
        def response = runExampleTask(CHALLENGE_ID, TEAM_ID)

        then:
        response.expectStatus().isNotFound()
    }

    def "should give max score when a solution of an example task is right"(){
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

    def "should give 0 score when a solution of an example task is wrong"(){
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

    def "should give 0 score when a client's response can't be parsed to a required type"(){
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

    private void registerTeam(String teamId) {
        webClient.post()
                .uri('/registration')
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just("""{ "name": "$teamId"}""".toString()), String)
                .exchange()
                .expectStatus().is2xxSuccessful()
    }

    void stubTeamResponse(String endpoint, String response, int status = 200, int latency = 0) {
        stubFor(get(urlEqualTo(endpoint))
                .willReturn(aResponse()
                .withStatus(status)
                .withFixedDelay(latency)
                .withBody(response)))
    }
}
