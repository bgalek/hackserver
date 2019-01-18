package pl.allegro.tech.leaders.hackathon.challenge

import pl.allegro.tech.leaders.hackathon.challenge.base.CountChallengeDefinition

import static pl.allegro.tech.leaders.hackathon.challenge.base.SampleChallenges.COUNT_CHALLENGE

class ChallengeRegistrationSpec extends ChallengeSpec {
    def 'should throw error when registering two challenge definitions with the same id'() {
        given: 'there is a challenge definition registered'
            registerChallengeDefinitions(COUNT_CHALLENGE)
        when: 'different definition is registered with the same id'
            registerChallengeDefinitions(new SecondCountChallengeDefinition())
        then: 'an error is thrown'
            IllegalArgumentException exception = thrown(IllegalArgumentException)
            exception.message == "Duplicated challenge definition id: " + COUNT_CHALLENGE.id
    }

    def 'should not throw error when registering the same instance of challenge definition two times'() {
        given: 'there is a challenge definition registered'
            registerChallengeDefinitions(COUNT_CHALLENGE)
        when: 'different definition is registered with the same id'
            registerChallengeDefinitions(COUNT_CHALLENGE)
        then:
            noExceptionThrown()
    }
}

class SecondCountChallengeDefinition extends CountChallengeDefinition {}