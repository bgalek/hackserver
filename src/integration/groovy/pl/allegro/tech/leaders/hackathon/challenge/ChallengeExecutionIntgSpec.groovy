package pl.allegro.tech.leaders.hackathon.challenge

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.util.UriComponentsBuilder
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
    CalculatorChallengeDefinition challenge
    MockWebServer mockWebServer = new MockWebServer()

    String CHALLENGE_ID
    List<TaskDefinition> CHALLENGE_TASKS

    void setup() {
        CHALLENGE_ID = challenge.id
        CHALLENGE_TASKS = challenge.tasks
    }

    def 'should execute challenge through facade and store the results'() {
        given: 'a team is registered'
            TeamRegistration registration = new TeamRegistration(TEAM_ID,
                    new InetSocketAddress('127.0.0.1', findAvailableTcpPort()))
            registrationFacade.register(registration).block()
            mockWebServer.start(InetAddress.getByName('127.0.0.1'), registration.remoteAddress.port)
        and: 'a challenge is activated'
            challengeFacade.activateChallenge(CHALLENGE_ID).block()
        and: 'team responds with 4 correct and 2 incorrect results'
            stubTeamResponse(url(CHALLENGE_TASKS[0]), expectedSolution(CHALLENGE_TASKS[0])) // correct
            stubTeamResponse(url(CHALLENGE_TASKS[1]), "80808080808") // incorrect
            stubTeamResponse(url(CHALLENGE_TASKS[2]), expectedSolution(CHALLENGE_TASKS[2])) // correct
            stubTeamResponse(url(CHALLENGE_TASKS[3]), expectedSolution(CHALLENGE_TASKS[3])) // correct
            stubTeamResponse(url(CHALLENGE_TASKS[4]), "0") // incorrect
            stubTeamResponse(url(CHALLENGE_TASKS[5]), expectedSolution(CHALLENGE_TASKS[5])) // correct
        when: 'challenge is executed'
            challengeFacade.runChallenge(CHALLENGE_ID).blockLast()
        and: 'results are fetched'
            List<TaskResult> results = extract(challengeFacade.getResultsForChallenge(CHALLENGE_ID))
        then: 'team has 6 results where one has zero score'
            expectChallengeResult(results, TEAM_ID, CHALLENGE_ID)
                    .hasSize(6)
                    .hasNonZeroScoreForTask(CHALLENGE_TASKS[0])
                    .hasZeroScoreForTask(CHALLENGE_TASKS[1])
                    .hasNonZeroScoreForTask(CHALLENGE_TASKS[2])
                    .hasNonZeroScoreForTask(CHALLENGE_TASKS[3])
                    .hasZeroScoreForTask(CHALLENGE_TASKS[4])
        and: 'expect team challenge scores to be the sum of task results'
            webClient.get().uri("/scores/$CHALLENGE_ID").exchange()
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

    String url(TaskDefinition task) {
        return UriComponentsBuilder
                .newInstance()
                .path(challenge.getChallengeEndpoint())
                .queryParams(new LinkedMultiValueMap<>(task.getParameters()))
                .build(task.isParametersEncoded())
                .toUri()
    }


    String expectedSolution(TaskDefinition task) {
        return task.getExpectedSolution().toString()
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
