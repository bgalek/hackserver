package pl.allegro.tech.leaders.hackathon.challenge


import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeDetails
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeNotFoundException

import static pl.allegro.tech.leaders.hackathon.challenge.base.ChallengeDetailsAssertions.expectChallengeDetails
import static pl.allegro.tech.leaders.hackathon.challenge.base.SampleChallenges.COUNT_CHALLENGE
import static pl.allegro.tech.leaders.hackathon.challenge.base.SampleChallenges.SUM_CHALLENGE

class ChallengeActivationSpec extends ChallengeSpec {
    def 'should active challenges'() {
        given: 'there are two registered challenges'
            registerChallengeDefinitions(COUNT_CHALLENGE, SUM_CHALLENGE)

        when: 'first challenge is activated'
            activeChallenge(COUNT_CHALLENGE.id)
        and: 'active challenges are fetched'
            List<ChallengeDetails> activeChallenges = getActiveChallenges()
        then: 'fetch result contains only the activated challenge'
            activeChallenges.size() == 1
            expectActivatedChallenge(activeChallenges[0], COUNT_CHALLENGE)

        when: 'second challenge is activated'
            activeChallenge(SUM_CHALLENGE.id)
        and: 'active challenges are fetched'
            activeChallenges = getActiveChallenges()
        then: 'fetch result contains both activated challenges'
            activeChallenges.size() == 2
            expectActivatedChallenge(activeChallenges[0], COUNT_CHALLENGE)
            expectActivatedChallenge(activeChallenges[1], SUM_CHALLENGE)
    }

    def 'should list no active challenge when no challenge is registered'() {
        when: 'active challenges are fetched when none is registered'
            List<ChallengeDetails> result = getActiveChallenges()
        then: 'the result is empty'
            result.isEmpty()
    }

    def 'should list no active challenge before activation'() {
        given: 'there are two registered challenges'
            registerChallengeDefinitions(COUNT_CHALLENGE, SUM_CHALLENGE)
        when: 'active challenges are fetched before activation'
            List<ChallengeDetails> result = getActiveChallenges()
        then: 'the result is empty'
            result.isEmpty()
    }

    def 'should throw error when activating non existing challenge'() {
        when: 'non existing challenge is activated'
            activeChallenge('non-existing')
        then: 'an error is thrown'
            thrown(ChallengeNotFoundException)
    }

    def 'should not throw error after second activation of the same challenge'() {
        given: 'there are two registered challenges'
            registerChallengeDefinitions(COUNT_CHALLENGE, SUM_CHALLENGE)
        and: 'one is activated'
            activeChallenge(SUM_CHALLENGE.id)
        when: 'the same challenge is activated for the second time'
            activeChallenge(SUM_CHALLENGE.id)
        then: 'no exception is thrown'
            noExceptionThrown()
        and: 'active challenges are fetched'
            ChallengeDetails challengeDetails = getActiveChallenges()[0]
        and: 'challenge is still active'
            challengeDetails.id == SUM_CHALLENGE.id
            challengeDetails.active
            challengeDetails.activatedAt != null
    }

    def 'should not deactivate challenge when its registered for the second time'() {
        given: 'there is a registered challenge'
            registerChallengeDefinitions(COUNT_CHALLENGE)
        and: 'challenge is activated'
            activeChallenge(COUNT_CHALLENGE.id)
        when: 'the same challenge is registered for the second time'
            registerChallengeDefinitions(COUNT_CHALLENGE)
        and: 'active challenges are fetched'
            List<ChallengeDetails> activeChallenges = getActiveChallenges()
        then: 'the challenge is still active'
            activeChallenges.size() == 1
            expectActivatedChallenge(activeChallenges[0], COUNT_CHALLENGE)
    }

    private void expectActivatedChallenge(ChallengeDetails challengeDetails, ChallengeDefinition definition) {
        expectChallengeDetails(challengeDetails)
                .toBeDerivedFrom(definition)
                .toBeActivatedAt(clock.instant())
    }
}
