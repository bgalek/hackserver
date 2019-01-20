package pl.allegro.tech.leaders.hackathon.runner

import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.ClassRule
import org.springframework.http.MediaType
import pl.allegro.tech.leaders.hackathon.base.IntegrationSpec
import reactor.core.publisher.Mono
import spock.lang.Shared

import static com.github.tomakehurst.wiremock.client.WireMock.*

class TaskRunnerSpec extends IntegrationSpec {
    def CHALLENGE_ID = 'calculator-challenge'
    def CHALLENGE_ENDPOINT = '/calc'
    def TEAM_ID = 'team-a'

    @ClassRule
    @Shared
    public WireMockRule wireMock = new WireMockRule(8080)

    def "should return 404 when running an example task for an unregistered team"(){
      given:
      activateChallenge(CHALLENGE_ID)

      when:
      def response = webClient.get().uri("/run/example/$CHALLENGE_ID?team-id=$TEAM_ID").exchange()

      then:
      response.expectStatus().isNotFound()
    }

    def "should return 404 when running an example task on inactive challenge"(){
        given:
        registerTeam(TEAM_ID)

        when:
        def response = webClient.get().uri("/run/example/$CHALLENGE_ID?team-id=$TEAM_ID").exchange()

        then:
        response.expectStatus().isNotFound()
    }

    def "should give max score when a solution of an example task is right"(){
        given:
        activateChallenge(CHALLENGE_ID)
        registerTeam(TEAM_ID)
        stubTeamResponse(CHALLENGE_ENDPOINT, "4")

        when:
        def exchange = webClient.get()
                .uri("/run/example/$CHALLENGE_ID?team-id=$TEAM_ID")
                .exchange()

        then:
        exchange.expectStatus().is2xxSuccessful()
        exchange.expectBody().jsonPath('.score').isEqualTo(1)
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

    void stubTeamResponse(String endpoint, String response) {
        stubFor(get(urlEqualTo(endpoint))
                .willReturn(aResponse()
                .withStatus(200)
                .withBody(response)))
    }
}
