package pl.allegro.tech.leaders.hackathon.challenge

import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import pl.allegro.tech.leaders.hackathon.base.IntegrationSpec

import static org.hamcrest.Matchers.empty
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ChallengeDeactivationIntgSpec extends IntegrationSpec {
    private static final String CALCULATOR_CHALLENGE_ID = 'calculator-challenge'
    private static final String TWITTER_CHALLENGE_ID = 'twitter-challenge'

    def 'should deactivate the calculator challenge'() {
        given: 'calculator challenge is activated'
            mockMvcClient.put("/challenges/${CALCULATOR_CHALLENGE_ID}/activate")
                    .andExpect(status().is2xxSuccessful())
        and: 'twitter challenge is activated'
            mockMvcClient.put("/challenges/${TWITTER_CHALLENGE_ID}/activate")
                    .andExpect(status().is2xxSuccessful())
        when: 'calculator challenge is deactivated'
            mockMvcClient.put("/challenges/${CALCULATOR_CHALLENGE_ID}/deactivate")
                    .andExpect(status().is2xxSuccessful())
        then: 'activated challenges contain only the twitter challenge'
            mockMvcClient.get('/challenges')
                    .andExpect(jsonPath('$', hasSize(1)))
                    .andExpect(MockMvcResultMatchers.jsonPath('$[0].id', is(TWITTER_CHALLENGE_ID)))

        when: 'twitter challenge is successfully deactivated'
            mockMvcClient.put("/challenges/${TWITTER_CHALLENGE_ID}/deactivate")
                    .andExpect(status().is2xxSuccessful())
        then: 'activated challenges are empty'
            mockMvcClient.get('/challenges')
                    .andExpect(jsonPath('$', hasSize(0)))
    }

    def 'should return 404 when deactivating non existing challenge'() {
        when: 'deactivating an unknown challenge'
            ResultActions response = mockMvcClient.put('/challenges/unknown/deactivate')
        then: 'response status code is 404'
            response
                    .andExpect(status().isNotFound())
    }
}
