package pl.allegro.tech.leaders.hackathon.challenge

import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeResultDto
import pl.allegro.tech.leaders.hackathon.registration.api.RegisteredTeam
import reactor.core.publisher.Mono
import spock.lang.Ignore

import static pl.allegro.tech.leaders.hackathon.challenge.base.ReactorValueExtractor.extract
import static pl.allegro.tech.leaders.hackathon.challenge.base.SampleChallenges.SAMPLE_CHALLENGE

class ChallengeExecutionSpec extends ChallengeSpec {
    @Ignore
    def 'should execute a challenge on all teams'() {
        given: 'there are two teams registered'
            RegisteredTeam firstTeam = registerTeam()
            RegisteredTeam secondTeam = registerTeam()

        when: 'a challenge is executed'
            executeChallange()
        then: 'the first team was challenged with all the tasks'
            (1.._) * solutionClient.execute(firstTeam.uri) >> invalidResult()
        and: 'the second team was challenged with all the tasks'
            (1.._) * solutionClient.execute(secondTeam.uri) >> invalidResult()

        when: 'challenge results are fetched'
            List<ChallengeResultDto> results = extract(facade.getChallengeResult ( SAMPLE_CHALLENGE.id))
        then: 'challenge result contains information for both teams'
            results == [
                    invalidChallengeResult(firstTeam.id),
                    invalidChallengeResult(secondTeam.id)
            ]
    }

    @Ignore
    def 'should calculate a challenge result from resolved tasks'() {
        given: 'there is a team registered'
            RegisteredTeam team = registerTeam()
        when: 'a challenge is executed'
            List<ChallengeResultDto> results = executeChallange()
        then: 'the team resolved half of the tasks'
            1 * solutionClient.execute(team.uri) >> invalidResult()
            1 * solutionClient.execute(team.uri) >> validResult()
        and: 'team received half of the challenge points'
            results == [
                    new ChallengeResultDto() // TODO: check if the result contains half of the challenge points and proper details
            ]
    }

    @Ignore
    def 'when communication fails team should receive no points'() {
        given: 'there is a team registered'
            RegisteredTeam team = registerTeam()
        when: 'a challenge is executed'
            List<ChallengeResultDto> results = executeChallange()
        then: 'the team fails to answer'
            1 * solutionClient.execute(team.uri) >> Mono.error(new RuntimeException())
        then: 'team receives no points'
            results == [
                    new ChallengeResultDto() // TODO: check if the result contains 0 points and a proper message
            ]
    }

    private List<ChallengeResultDto> executeChallange() {
        return extract(facade.executeChallange(SAMPLE_CHALLENGE.id))
    }

    private RegisteredTeam registerTeam() {
        // TOOD: register a team with sample data
        return new RegisteredTeam()
    }

    private ChallengeResultDto invalidChallengeResult(String teamId, String challangeId = SAMPLE_CHALLENGE.id) {
        // TOOD: create an invalid challenge result
        return new ChallengeResultDto()
    }

    private Mono<String> invalidResult() {
        // TOOD: create an invalid result
        return Mono.just("Invalid result")
    }

    private Mono<String> validResult() {
        // TOOD: create a valid result
        return Mono.just("Valid result")
    }
}
