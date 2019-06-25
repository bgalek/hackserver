package pl.allegro.tech.leaders.hackathon.challenge

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient
import pl.allegro.tech.leaders.hackathon.base.IntegrationSpec
import pl.allegro.tech.leaders.hackathon.challenge.samples.CalculatorChallengeDefinition
import pl.allegro.tech.leaders.hackathon.challenge.samples.HostnameChallengeDefinition

class ChallengeDeactivationIntgSpec extends IntegrationSpec {

    String CHALLENGE_X_ID
    String CHALLENGE_Y_ID

    @Autowired
    CalculatorChallengeDefinition challengeX

    @Autowired
    HostnameChallengeDefinition challengeY

    void setup() {
        CHALLENGE_X_ID = challengeX.id
        CHALLENGE_Y_ID = challengeY.id
    }

    def 'should deactivate the calculator challenge'() {
        given: 'calculator challenge is activated'
            webClient.put().uri("/challenges/${CHALLENGE_X_ID}/activate").exchange()
                    .expectStatus().is2xxSuccessful()
        and: 'twitter challenge is activated'
            webClient.put().uri("/challenges/${CHALLENGE_Y_ID}/activate").exchange()
                    .expectStatus().is2xxSuccessful()
        when: 'calculator challenge is deactivated'
            webClient.put().uri("/challenges/${CHALLENGE_X_ID}/deactivate").exchange()
                    .expectStatus().is2xxSuccessful()
        then: 'activated challenges contain only the twitter challenge'
            webClient.get().uri('/challenges').exchange()
                    .expectBody()
                    .jsonPath('$.length()').isEqualTo(1)
                    .jsonPath('$[0].id').isEqualTo(CHALLENGE_Y_ID)
        when: 'twitter challenge is successfully deactivated'
            webClient.put().uri("/challenges/${CHALLENGE_Y_ID}/deactivate").exchange()
                    .expectStatus().is2xxSuccessful()
        then: 'activated challenges are empty'
            webClient.get().uri('/challenges').exchange()
                    .expectBody()
                    .jsonPath('$.length()').isEqualTo(0)
    }

    def 'should return 404 when deactivating non existing challenge'() {
        when: 'deactivating an unknown challenge'
            WebTestClient.ResponseSpec response = webClient.put().uri('/challenges/unknown/deactivate').exchange()
        then: 'response status code is 404'
            response
                    .expectStatus().isNotFound()
    }
}
