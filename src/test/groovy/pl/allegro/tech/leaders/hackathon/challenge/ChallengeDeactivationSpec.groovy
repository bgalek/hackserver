package pl.allegro.tech.leaders.hackathon.challenge

import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeDetails
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeNotFoundException

import static pl.allegro.tech.leaders.hackathon.challenge.base.SampleChallenges.COUNT_CHALLENGE
import static pl.allegro.tech.leaders.hackathon.challenge.base.SampleChallenges.SUM_CHALLENGE

class ChallengeDeactivationSpec extends ChallengeSpec {
    def 'should deactivate challenges'() {
        given: 'there are two registered challenges'
            registerChallengeDefinitions(COUNT_CHALLENGE, SUM_CHALLENGE)
        and: 'both of them are activated'
            activeChallenges(COUNT_CHALLENGE.id, SUM_CHALLENGE.id)

        when: 'first challenge is deactivated'
            deactivateChallenge(COUNT_CHALLENGE.id)
        and: 'activated challenges are fetched'
            List<ChallengeDetails> activeChallenges = getActiveChallenges()
        then: 'fetch result contains only the second challenge'
            activeChallenges.size() == 1
            activeChallenges[0].id == SUM_CHALLENGE.id

        when: 'second challenge is deactivated'
            deactivateChallenge(SUM_CHALLENGE.id)
        and: 'activated challenges are fetched'
            activeChallenges = getActiveChallenges()
        then: 'fetch result contains no challenges'
            activeChallenges.size() == 0
    }

    def 'should throw error when fetching deactivated challenge by id'() {
        given: 'there are two registered challenges'
            registerChallengeDefinitions(COUNT_CHALLENGE, SUM_CHALLENGE)
        and: 'both of them are activated'
            activeChallenges(COUNT_CHALLENGE.id, SUM_CHALLENGE.id)

        when: 'first challenge is deactivated'
            deactivateChallenge(COUNT_CHALLENGE.id)
        and: 'first challenge is fetched by id'
            getActiveChallenge(COUNT_CHALLENGE.id)
        then:
            thrown(ChallengeNotFoundException)
    }

    def 'should throw error when deactivating non existing challenge'() {
        when: 'non existing challenge is deactivated'
            deactivateChallenge('non-existing')
        then: 'an error is thrown'
            thrown(ChallengeNotFoundException)
    }
}
