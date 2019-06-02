package pl.allegro.tech.leaders.hackathon.challenge

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import pl.allegro.tech.leaders.hackathon.base.IntegrationSpec
import pl.allegro.tech.leaders.hackathon.challenge.api.TaskResult
import pl.allegro.tech.leaders.hackathon.challenge.samples.CalculatorChallengeDefinition
import pl.allegro.tech.leaders.hackathon.registration.RegistrationFacade
import pl.allegro.tech.leaders.hackathon.registration.api.TeamRegistration

import static org.springframework.util.SocketUtils.findAvailableTcpPort
import static pl.allegro.tech.leaders.hackathon.challenge.base.ChallengeResultAssertions.expectChallengeResult
import static pl.allegro.tech.leaders.hackathon.challenge.base.ReactorValueExtractor.extract

class ChallengeExecutionIntgSpec extends IntegrationSpec {
    private static final String TEAM_ID = "team-a"
    @Autowired
    ChallengeFacade challengeFacade
    @Autowired
    RegistrationFacade registrationFacade
    @Autowired
    CalculatorChallengeDefinition calculatorChallengeDefinition
    MockWebServer mockWebServer = new MockWebServer()

    String CALCULATOR_CHALLENGE_ID
    List<TaskDefinition> CALCULATOR_CHALLENGE_TASKS

    void setup() {
        CALCULATOR_CHALLENGE_ID = calculatorChallengeDefinition.id
        CALCULATOR_CHALLENGE_TASKS = calculatorChallengeDefinition.tasks
    }

    def 'should execute challenge through facade and store the results'() {
        given: 'a team is registered'
            TeamRegistration registration = new TeamRegistration(TEAM_ID,
                    new InetSocketAddress('127.0.0.1', findAvailableTcpPort()))
            registrationFacade.register(registration).block()
            mockWebServer.start(InetAddress.getByName('127.0.0.1'), registration.remoteAddress.port)
        and: 'a challenge is activated'
            challengeFacade.activateChallenge(CALCULATOR_CHALLENGE_ID).block()
        and: 'team responds with on correct and 2 incorrect results'
            stubTeamResponse("/calc?equation=2+2", "4") // correct
            stubTeamResponse("/calc?equation=2+2*2", "6") // correct
            stubTeamResponse("/calc?equation=(2+2)*2", "4") // incorrect
        when: 'challenge is executed'
            challengeFacade.runChallenge(CALCULATOR_CHALLENGE_ID).blockLast()
        and: 'results are fetched'
            List<TaskResult> results = extract(challengeFacade.getResultsForChallenge(CALCULATOR_CHALLENGE_ID))
        then: 'team has 3 results where one has zero score'
            expectChallengeResult(results, TEAM_ID, CALCULATOR_CHALLENGE_ID)
                    .hasSize(3)
                    .hasNonZeroScoreForTask(CALCULATOR_CHALLENGE_TASKS[0])
                    .hasNonZeroScoreForTask(CALCULATOR_CHALLENGE_TASKS[1])
                    .hasZeroScoreForTask(CALCULATOR_CHALLENGE_TASKS[2])
        and: 'expect team challenge scores to be the sum of task results'
            webClient.get().uri("/scores/$CALCULATOR_CHALLENGE_ID").exchange()
                    .expectBody()
                    .jsonPath('$.scores.length()').isEqualTo(1)
                    .jsonPath('$.scores[0].teamId').isEqualTo(TEAM_ID)
                    .jsonPath('$.scores[0].score').isEqualTo(results.sum { it.score })
        and: 'expect team hackaton scores to be equal challenge scores'
            webClient.get().uri("/scores").exchange()
                    .expectBody()
                    .jsonPath('$.scores.length()').isEqualTo(1)
                    .jsonPath('$.scores[0].teamId').isEqualTo(TEAM_ID)
                    .jsonPath('$.scores[0].score').isEqualTo(results.sum { it.score })
    }

    void stubTeamResponse(String url, String response) {
        mockWebServer.url(url)
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(response))
    }

    void cleanup() {
        mockWebServer.shutdown()
    }
}
