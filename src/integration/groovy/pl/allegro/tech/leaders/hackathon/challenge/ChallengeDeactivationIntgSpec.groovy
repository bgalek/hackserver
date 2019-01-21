package pl.allegro.tech.leaders.hackathon.challenge

import org.springframework.test.web.reactive.server.WebTestClient
import pl.allegro.tech.leaders.hackathon.base.IntegrationSpec

class ChallengeDeactivationIntgSpec extends IntegrationSpec {
    private static final String CALCULATOR_CHALLENGE_ID = 'calculator-challenge'
    private static final String TWITTER_CHALLENGE_ID = 'twitter-challenge'

    def 'should deactivate the calculator challenge'() {
        given: 'calculator challenge is activated'
            webClient.put().uri("/challenges/${CALCULATOR_CHALLENGE_ID}/activate").exchange()
                    .expectStatus().is2xxSuccessful()
        and: 'twitter challenge is activated'
            webClient.put().uri("/challenges/${TWITTER_CHALLENGE_ID}/activate").exchange()
                    .expectStatus().is2xxSuccessful()
        when: 'calculator challenge is deactivated'
            webClient.put().uri("/challenges/${CALCULATOR_CHALLENGE_ID}/deactivate").exchange()
                    .expectStatus().is2xxSuccessful()
        then: 'activated challenges contain only the twitter challenge'
            webClient.get().uri('/challenges').exchange()
                    .expectBody()
                    .jsonPath('$.length()').isEqualTo(1)
                    .jsonPath('$[0].id').isEqualTo(TWITTER_CHALLENGE_ID)
        when: 'twitter challenge is successfully deactivated'
            webClient.put().uri("/challenges/${TWITTER_CHALLENGE_ID}/deactivate").exchange()
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
