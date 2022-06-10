package pl.allegro.tech.leaders.hackathon.challenge

import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeDetails
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeNotFoundException

import static pl.allegro.tech.leaders.hackathon.challenge.base.SampleChallenges.COUNT_CHALLENGE
import static pl.allegro.tech.leaders.hackathon.challenge.base.SampleChallenges.SUM_CHALLENGE

class ChallengeActivationSpec extends ChallengeSpec {
//     def 'should activate challenges'() {
//         given: 'there are two registered challenges'
//             registerChallengeDefinitions(COUNT_CHALLENGE, SUM_CHALLENGE)
//         when: 'first challenge is activated'
//             activateChallenge(COUNT_CHALLENGE.id)
//         and: 'activated challenges are fetched'
//             List<ChallengeDetails> activeChallenges = getActiveChallenges()
//         then: 'fetch result contains only the activated challenge'
//             activeChallenges.size() == 1
//             ChallengeDetails challengeDetails = activeChallenges[0]
//             challengeDetails.id == COUNT_CHALLENGE.id
//         and: 'challenge from the result is well described'
//             challengeDetails.name == COUNT_CHALLENGE.name
//             challengeDetails.description == COUNT_CHALLENGE.description
//             challengeDetails.challengeEndpoint == COUNT_CHALLENGE.challengeEndpoint
//             challengeDetails.challengeParameters == COUNT_CHALLENGE.challengeParams
//             challengeDetails.challengeResponse == COUNT_CHALLENGE.challengeResponse
//             challengeDetails.activatedAt == clock.instant()

        when: 'second challenge is activated'
            activateChallenge(SUM_CHALLENGE.id)
        and: 'activated challenges are fetched'
            activeChallenges = getActiveChallenges()
        then: 'fetch result contains both activated challenges'
            activeChallenges.size() == 2
            activeChallenges[0].id == COUNT_CHALLENGE.id
            activeChallenges[1].id == SUM_CHALLENGE.id
    }

    def 'should serve activated challenge by id'() {
        given: 'there are two registered challenges'
            registerChallengeDefinitions(COUNT_CHALLENGE, SUM_CHALLENGE)

        when: 'first challenge is fetched by id before activation'
            getActiveChallenge(COUNT_CHALLENGE.id)
        then:
            thrown(ChallengeNotFoundException)

        when: 'first challenge is activated'
            activateChallenge(COUNT_CHALLENGE.id)
        then: 'first challenge can be fetched by id'
            getActiveChallenge(COUNT_CHALLENGE.id).id == COUNT_CHALLENGE.id
    }

    def 'should serve no challenges when no challenge is registered'() {
        when: 'challenges are fetched'
            List<ChallengeDetails> challenges = getActiveChallenges()
        then: 'the result is empty'
            challenges.isEmpty()
        when: 'challenge is fetched by id'
            getActiveChallenge(COUNT_CHALLENGE.id)
        then:
            thrown(ChallengeNotFoundException)
    }

    def 'should throw error when activating non existing challenge'() {
        when: 'non existing challenge is activated'
            activateChallenge('non-existing')
        then: 'an error is thrown'
            thrown(ChallengeNotFoundException)
    }

    def 'should accept second activation of the same challenge'() {
        given: 'there are two registered challenges'
            registerChallengeDefinitions(COUNT_CHALLENGE, SUM_CHALLENGE)
        and: 'one is activated'
            activateChallenge(SUM_CHALLENGE.id)
        when: 'the same challenge is activated for the second time'
            activateChallenge(SUM_CHALLENGE.id)
        then: 'no exception is thrown'
            noExceptionThrown()
        and: 'activated challenges are fetched'
            ChallengeDetails challengeDetails = getActiveChallenges()[0]
        and: 'challenge is still activated'
            challengeDetails.id == SUM_CHALLENGE.id
            challengeDetails.active
            challengeDetails.activatedAt != null
    }

    def 'should not deactivate challenge when its registered for the second time'() {
        given: 'there is a registered challenge'
            registerChallengeDefinitions(COUNT_CHALLENGE)
        and: 'challenge is activated'
            activateChallenge(COUNT_CHALLENGE.id)
        when: 'the same challenge is registered for the second time'
            registerChallengeDefinitions(COUNT_CHALLENGE)
        and: 'activated challenges are fetched'
            List<ChallengeDetails> activeChallenges = getActiveChallenges()
        then: 'the challenge is still activated'
            activeChallenges.size() == 1
            activeChallenges[0].id == COUNT_CHALLENGE.id
    }
}
