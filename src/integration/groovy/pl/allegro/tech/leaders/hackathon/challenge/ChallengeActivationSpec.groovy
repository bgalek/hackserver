package pl.allegro.tech.leaders.hackathon.challenge

import org.springframework.test.web.servlet.ResultActions
import pl.allegro.tech.leaders.hackathon.base.IntegrationSpec

import static org.hamcrest.Matchers.empty
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ChallengeActivationSpec extends IntegrationSpec {
    private static final String CALCULATOR_CHALLENGE_ID = 'calculator-challenge'
    private static final String TWITTER_CHALLENGE_ID = 'twitter-challenge'

    def 'should return empty result when no challenge is activated'() {
        when: 'active challenges are fetched before activating any'
            ResultActions response = mockMvcClient.get('/challenges')
        then: 'result is empty'
            response
                    .andExpect(jsonPath('$', is(empty())))
    }

    def 'should activate the calculator challenge'() {
        when: 'challenge is successfully activated'
            mockMvcClient.put("/challenges/${CALCULATOR_CHALLENGE_ID}/activate")
                    .andExpect(status().is2xxSuccessful())
        then: 'active challenges contain activated challenge'
            mockMvcClient.get('/challenges')
                    .andExpect(jsonPath('$', hasSize(1)))
                    .andExpect(jsonPath('$[0].id', is(CALCULATOR_CHALLENGE_ID)))

        when: 'second challenge is successfully activated'
            mockMvcClient.put("/challenges/${TWITTER_CHALLENGE_ID}/activate")
                    .andExpect(status().is2xxSuccessful())
        then: 'result contains both activated challenges sorted by activation time'
            mockMvcClient.get('/challenges')
                    .andExpect(jsonPath('$', hasSize(2)))
                    .andExpect(jsonPath('$[0].id', is(TWITTER_CHALLENGE_ID)))
                    .andExpect(jsonPath('$[1].id', is(CALCULATOR_CHALLENGE_ID)))
    }

    def 'should return 404 when activating non existing challenge'() {
        when: 'activating unknown challenge'
            ResultActions response = mockMvcClient.put('/challenges/unknown/activate')
        then: 'response status code is 404'
            response
                    .andExpect(status().isNotFound())
    }
}
