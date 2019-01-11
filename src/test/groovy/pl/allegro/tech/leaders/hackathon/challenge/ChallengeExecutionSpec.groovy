package pl.allegro.tech.leaders.hackathon.challenge

import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeResultDto
import pl.allegro.tech.leaders.hackathon.challenge.base.SpecSimulatedException
import pl.allegro.tech.leaders.hackathon.registration.api.RegisteredTeam
import reactor.core.publisher.Mono
import spock.lang.Ignore

import static pl.allegro.tech.leaders.hackathon.challenge.base.ReactorValueExtractor.extract
import static pl.allegro.tech.leaders.hackathon.challenge.base.SampleChallenges.SAMPLE_CHALLENGE

class ChallengeExecutionSpec extends ChallengeSpec {
    @Ignore
    def 'should execute a challenge on all teams'() {
        given: 'there is a team service returning a valid solution'
            RegisteredTeam firstTeam = registerTeam()
            solutionClient.recordResponse(firstTeam.uri, validResult()) // TODO: should use task uri instead of team uri
        and: 'there is a second team service returning an invalid solution'
            RegisteredTeam secondTeam = registerTeam()
            solutionClient.recordResponse(secondTeam.uri, invalidResult())
        when: 'challenge is executed'
            executeChallenge()
        then: 'challenge result contains information on both teams'
            List<ChallengeResultDto> results = extract(facade.getChallengeResult(SAMPLE_CHALLENGE.id))
            results == [
                    invalidChallengeResult(firstTeam.id),
                    invalidChallengeResult(secondTeam.id)
            ]
    }

    @Ignore
    def 'should calculate a challenge result from resolved tasks'() {
        given: 'there is a registered team service'
            RegisteredTeam team = registerTeam()
        and: 'team provides one 1 valid solution out of 3 tasks'
            solutionClient
                    .recordResponse(team.uri, validResult())
                    .recordResponse(team.uri, invalidResult())
                    .recordResponse(team.uri, invalidResult())
        when: 'challenge is executed'
            List<ChallengeResultDto> results = executeChallenge()
        then: 'team received 1/3 of the challenge points'
            results == [
                    new ChallengeResultDto() // TODO: check if the result contains half of the challenge points and proper details
            ]
    }

    @Ignore
    def 'should assign 0 points when communication to a team service fails'() {
        given: 'there is a team registered'
            RegisteredTeam team = registerTeam()
        and: 'there is no connection to the team service'
            solutionClient.recordResponse(team.uri, SpecSimulatedException.asMono())
        when: 'a challenge is executed'
            List<ChallengeResultDto> results = executeChallenge()
        then: 'team receives no points'
            results == [
                    new ChallengeResultDto() // TODO: check if the result contains 0 points and a proper message
            ]
    }

    private List<ChallengeResultDto> executeChallenge() {
        return extract(facade.executeChallenge(SAMPLE_CHALLENGE.id))
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
