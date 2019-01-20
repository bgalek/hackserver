package pl.allegro.tech.leaders.hackathon.challenge

import org.springframework.test.web.reactive.server.WebTestClient
import pl.allegro.tech.leaders.hackathon.base.IntegrationSpec

class ChallengeActivationSpec extends IntegrationSpec {
    private static final String CALCULATOR_CHALLENGE_ID = 'calculator-challenge'
    private static final String TWITTER_CHALLENGE_ID = 'twitter-challenge'

    def 'should return empty result when no challenge is activated'() {
        when: 'active challenges are fetched before activating any'
            WebTestClient.ResponseSpec response = webClient.get().uri('/challenges').exchange()
        then: 'result is empty'
            response
                    .expectBody()
                    .jsonPath('$').isEmpty()
    }

    def 'should activate the calculator challenge'() {
        when: 'challenge is successfully activated'
            webClient.put().uri("/challenges/${CALCULATOR_CHALLENGE_ID}/activate").exchange()
                    .expectStatus()
                    .is2xxSuccessful()
        then: 'active challenges contain activated challenge'
            webClient.get().uri('/challenges').exchange()
                    .expectBody()
                    .jsonPath('$.length()').isEqualTo(1)
                    .jsonPath('$[0].id').isEqualTo(CALCULATOR_CHALLENGE_ID)
        when: 'second challenge is successfully activated'
            webClient.put().uri("/challenges/${TWITTER_CHALLENGE_ID}/activate").exchange()
                    .expectStatus()
                    .is2xxSuccessful()
        then: 'result contains both activated challenges sorted by activation time'
            webClient.get().uri('/challenges').exchange()
                    .expectBody()
                    .jsonPath('$.length()').isEqualTo(2)
                    .jsonPath('$[0].id').isEqualTo(TWITTER_CHALLENGE_ID)
                    .jsonPath('$[1].id').isEqualTo(CALCULATOR_CHALLENGE_ID)
    }

    def 'should return 404 when activating non existing challenge'() {
        when: 'activating unknown challenge'
            WebTestClient.ResponseSpec response = webClient.put().uri('/challenges/unknown/activate').exchange()
        then: 'response status code is 404'
            response
                    .expectStatus()
                    .isNotFound()
    }
}
