package pl.allegro.tech.leaders.hackathon.runner

import com.github.tomakehurst.wiremock.junit.WireMockRule
import groovy.json.JsonSlurper
import org.junit.ClassRule
import pl.allegro.tech.leaders.hackathon.base.IntegrationSpec
import spock.lang.Shared

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class TaskRunnerSpec extends IntegrationSpec {
    def CHALLENGE_ID = 'calculator-challenge'
    def CHALLENGE_ENDPOINT = '/calc'
    def TEAM_ID = 'team-a'

    @ClassRule
    @Shared
    public WireMockRule wireMock = new WireMockRule(8080)

    @Shared
    def jsonSlurper = new JsonSlurper()

    def "should return 404 when running an example task for an unregistered team"(){
      given:
      activateChallenge(CHALLENGE_ID)

      when:
      def response = mockMvcClient.get("/run/example/$CHALLENGE_ID?team-id=$TEAM_ID")

      then:
      response.andExpect(status().isNotFound())
    }

    def "should return 404 when running an example task on inactive challenge"(){
        given:
        registerTeam(TEAM_ID)

        when:
        def response = mockMvcClient.get("/run/example/$CHALLENGE_ID?team-id=$TEAM_ID")

        then:
        response.andExpect(status().isNotFound())
    }

    def "should give max score when a solution of an example task is right"(){
        given:
        activateChallenge(CHALLENGE_ID)
        registerTeam(TEAM_ID)
        stubTeamResponse(CHALLENGE_ENDPOINT, "4")

        when:
        def exampleRunResult = mockMvcClient.get("/run/example/$CHALLENGE_ID?team-id=$TEAM_ID")
                .andExpect(status().is2xxSuccessful())
                .andReturn()

        def resultAsMap = jsonSlurper.parseText(exampleRunResult.response.contentAsString)

        println "resultAsMap " + resultAsMap

        then:
        resultAsMap.score == 1
    }


    private void activateChallenge(String challengeId) {
        mockMvcClient.put("/challenges/$challengeId/activate")
                .andExpect(status().is2xxSuccessful())
    }

    private void registerTeam(String teamId) {
        mockMvcClient.post('/registration', """{ "name": "$teamId"}""")
                .andExpect(status().is2xxSuccessful())
    }

    void stubTeamResponse(String endpoint, String response) {
        stubFor(get(urlEqualTo(endpoint))
                .willReturn(aResponse()
                .withStatus(200)
                .withBody(response)))
    }
}
