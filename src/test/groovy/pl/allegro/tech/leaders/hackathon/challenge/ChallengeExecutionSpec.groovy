package pl.allegro.tech.leaders.hackathon.challenge

import org.junit.Before
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeNotFoundException
import pl.allegro.tech.leaders.hackathon.challenge.api.TaskResult
import pl.allegro.tech.leaders.hackathon.registration.api.TeamNotFoundException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

import static pl.allegro.tech.leaders.hackathon.challenge.base.ChallengeResultAssertions.expectChallengeResult
import static pl.allegro.tech.leaders.hackathon.challenge.base.CountChallengeDefinition.COUNT_FIRST_TASK
import static pl.allegro.tech.leaders.hackathon.challenge.base.CountChallengeDefinition.COUNT_SECOND_TASK
import static pl.allegro.tech.leaders.hackathon.challenge.base.ReactorValueExtractor.extract
import static pl.allegro.tech.leaders.hackathon.challenge.base.SampleChallenges.COUNT_CHALLENGE
import static pl.allegro.tech.leaders.hackathon.challenge.base.SampleChallenges.SAMPLE_CHALLENGES
import static pl.allegro.tech.leaders.hackathon.challenge.base.SampleRegisteredTeam.TEAM_A
import static pl.allegro.tech.leaders.hackathon.challenge.base.SampleRegisteredTeam.TEAM_B
import static pl.allegro.tech.leaders.hackathon.challenge.base.SampleRegisteredTeam.TEAM_C

class ChallengeExecutionSpec extends ChallengeSpec {

    @Before
    void registerSampleChallenges() {
        extract(facade.registerChallengeDefinitions(SAMPLE_CHALLENGES))
    }

    @Before
    void stubRegistrationFacade() {
        registrationFacade.all >> Flux.fromIterable([TEAM_A, TEAM_B])
        registrationFacade.getTeamById({ it == TEAM_A.id }) >> Mono.just(TEAM_A)
        registrationFacade.getTeamById({ it == TEAM_B.id }) >> Mono.just(TEAM_B)
        registrationFacade.getTeamById({ it == TEAM_C.id }) >> Mono.error(new TeamNotFoundException(TEAM_C.id))
    }

    def 'should throw error on executing a not activated challenge'() {
        when: 'not activated challenge is executed'
            runChallenge(COUNT_CHALLENGE.id)
        then: 'error is thrown'
            thrown(ChallengeNotFoundException)
    }

    def 'should throw error on executing an activated challenge on for a not registered team'() {
        given: 'a challenge is activated'
            activateChallenge(COUNT_CHALLENGE.id)
        when: 'not activated challenge is executed on non registered team'
            runChallenge(COUNT_CHALLENGE.id, TEAM_C.id)
        then: 'error is thrown'
            thrown(TeamNotFoundException)
    }

    def 'should execute a challenge on all registered teams'() {
        given: 'a challenge is activated'
            activateChallenge(COUNT_CHALLENGE.id)
        and: 'team A solves all tasks'
            teamClient.recordCorrectResponses(TEAM_A, COUNT_CHALLENGE)
        and: 'team B solves only one of two task'
            teamClient.recordIncorrectResponse(TEAM_B, COUNT_CHALLENGE, COUNT_FIRST_TASK)
            teamClient.recordCorrectResponse(TEAM_B, COUNT_CHALLENGE, COUNT_SECOND_TASK)
        when: 'the challenge is executed'
            runChallenge(COUNT_CHALLENGE.id)
        and: 'results are fetched'
            List<TaskResult> results = fetchResults(COUNT_CHALLENGE.id)
        then: 'the first team has max score for both tasks'
            expectChallengeResult(results, TEAM_A.id, COUNT_CHALLENGE.id)
                    .hasSize(2)
                    .hasMaxScoreForTask(COUNT_FIRST_TASK)
                    .hasMaxScoreForTask(COUNT_SECOND_TASK)
        and: 'the second team has 0 score for the first task and max score for the second one'
            expectChallengeResult(results, TEAM_B.id, COUNT_CHALLENGE.id)
                    .hasSize(2)
                    .hasZeroScoreForTask(COUNT_FIRST_TASK)
                    .hasMaxScoreForTask(COUNT_SECOND_TASK)
        and: 'every team was requested exactly 2 times - once per task'
            teamClient.requestCount(TEAM_A) == 2
            teamClient.requestCount(TEAM_B) == 2
    }

    def 'should continue challenge execution when one team does not respond'() {
        given: 'a challenge is activated'
            activateChallenge(COUNT_CHALLENGE.id)
        and: 'only team B responds to the challenge requests'
            teamClient.recordCorrectResponses(TEAM_B, COUNT_CHALLENGE)
        when: 'the challenge is executed'
            runChallenge(COUNT_CHALLENGE.id)
        and: 'results are fetched'
            List<TaskResult> results = fetchResults(COUNT_CHALLENGE.id)
        then: 'the first team 0 score for both tasks'
            expectChallengeResult(results, TEAM_A.id, COUNT_CHALLENGE.id)
                    .hasSize(2)
                    .hasZeroScoreForTask(COUNT_FIRST_TASK)
                    .hasZeroScoreForTask(COUNT_SECOND_TASK)
        and: 'the second team has 0 score for the first task and max score for the second one'
            expectChallengeResult(results, TEAM_B.id, COUNT_CHALLENGE.id)
                    .hasSize(2)
    }

    def 'should override previous results when challenge is rerun for a team for the second time'() {
        given: 'a challenge is activated'
            activateChallenge(COUNT_CHALLENGE.id)
        and: 'team A solves no task'
            teamClient.recordIncorrectResponses(TEAM_A, COUNT_CHALLENGE)
        and: 'team B solves all tasks'
            teamClient.recordCorrectResponses(TEAM_B, COUNT_CHALLENGE)

        when: 'the challenge is executed'
            runChallenge(COUNT_CHALLENGE.id)
        and: 'results are fetched'
            List<TaskResult> results = fetchResults(COUNT_CHALLENGE.id)
        then: 'team A has zero scores'
            expectChallengeResult(results, TEAM_A.id, COUNT_CHALLENGE.id)
                    .hasZeroScores(COUNT_CHALLENGE)
        and: 'team B has max scores'
            expectChallengeResult(results, TEAM_B.id, COUNT_CHALLENGE.id)
                    .hasMaxScores(COUNT_CHALLENGE)

        when: 'the team A fixes their solution and challenge is executed for the second time'
            teamClient.recordCorrectResponses(TEAM_A, COUNT_CHALLENGE)
            teamClient.recordIncorrectResponses(TEAM_B, COUNT_CHALLENGE)
            runChallenge(COUNT_CHALLENGE.id, TEAM_A.id)
        and: 'new results are fetched'
            results = fetchResults(COUNT_CHALLENGE.id)
        then: 'team A has updated max scores'
            expectChallengeResult(results, TEAM_A.id, COUNT_CHALLENGE.id)
                    .hasMaxScores(COUNT_CHALLENGE)
        and: 'team B scores did not change'
            expectChallengeResult(results, TEAM_B.id, COUNT_CHALLENGE.id)
                    .hasMaxScores(COUNT_CHALLENGE)
    }

    private List<TaskResult> runChallenge(String challengeId) {
        return extract(facade.runChallenge(challengeId))
    }

    private List<TaskResult> runChallenge(String challengeId, String teamId) {
        return extract(facade.runChallenge(challengeId, teamId))
    }

    private List<TaskResult> fetchResults(String challengeId) {
        return extract(facade.getResultsForChallenge(challengeId))
    }
}
