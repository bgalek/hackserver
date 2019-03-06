package pl.allegro.tech.leaders.hackathon.challenge

import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.ClassRule
import org.springframework.beans.factory.annotation.Autowired
import pl.allegro.tech.leaders.hackathon.base.IntegrationSpec
import pl.allegro.tech.leaders.hackathon.challenge.api.TaskResult
import pl.allegro.tech.leaders.hackathon.challenge.samples.CalcChallengeDefinition
import pl.allegro.tech.leaders.hackathon.registration.RegistrationFacade
import pl.allegro.tech.leaders.hackathon.registration.api.TeamRegistration
import pl.allegro.tech.leaders.hackathon.utils.InetUtils
import spock.lang.Shared

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import static pl.allegro.tech.leaders.hackathon.challenge.base.ChallengeResultAssertions.expectChallengeResult
import static pl.allegro.tech.leaders.hackathon.challenge.base.ReactorValueExtractor.extract

class ChallengeExecutionIntgSpec extends IntegrationSpec {
    private static final String TEAM_ID = "team-a"
    @Autowired
    ChallengeFacade challengeFacade
    @Autowired
    RegistrationFacade registrationFacade
    @ClassRule
    @Shared
    public WireMockRule wireMock = new WireMockRule(8080)

    def 'should execute challenge through facade and store the results'() {
        given: 'a team is registered'
            // TODO: without the option to specify a port it's impossible to test multiple teams
            registrationFacade.register(new TeamRegistration(TEAM_ID, InetUtils.fromString("127.0.0.1"))).block()
        and: 'a challenge is activated'
            challengeFacade.activateChallenge(CalcChallengeDefinition.ID).block()
        and: 'team responds with on correct and 2 incorrect results'
            stubTeamResponse("/calc?equation=2+2", "4") // correct
            stubTeamResponse("/calc?equation=2+2*2", "4") // incorrect
            stubTeamResponse("/calc?equation=(2+2)*2", "4") // incorrect
        when: 'challenge is executed'
            challengeFacade.runChallenge(CalcChallengeDefinition.ID).blockLast()
        and: 'results are fetched'
            List<TaskResult> results = extract(challengeFacade.getResultsForChallenge(CalcChallengeDefinition.ID))
        then: 'team has 3 results and only one has non zero score'
            expectChallengeResult(results, TEAM_ID, CalcChallengeDefinition.ID)
                    .hasSize(3)
                    .hasNonZeroScoreForTask(CalcChallengeDefinition.TASKS[0])
                    .hasZeroScoreForTask(CalcChallengeDefinition.TASKS[1])
                    .hasZeroScoreForTask(CalcChallengeDefinition.TASKS[2])

    }

    void stubTeamResponse(String url, String response) {
        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse()
                .withStatus(200)
                .withBody(response)))
    }
}
