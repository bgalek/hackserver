package pl.allegro.tech.leaders.hackathon

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.ResultActions
import pl.allegro.tech.leaders.hackathon.base.IntegrationSpec
import pl.allegro.tech.leaders.hackathon.registration.TeamRepository

import static java.util.UUID.randomUUID
import static org.hamcrest.Matchers.empty
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class RegistrationSpec extends IntegrationSpec {

    @Autowired TeamRepository teamRepository

    def 'empty team registry should serve no teams'() {
        given:
            teamRepository.deleteAll()

        when:
            ResultActions response = mockMvcClient.get('/registration')
        then:
            response
                    .andExpect(jsonPath('$', is(empty())))
    }

    def 'should add a team to a team registry'() {
        given:
            teamRepository.deleteAll()
            String teamName = randomUUID().toString()
            String registerTeamBody = """{ "name": "$teamName"}"""
        when:
            ResultActions registerResponse = mockMvcClient.post('/registration', registerTeamBody)
        then:
            registerResponse
                    .andExpect(status().isCreated())
                    .andExpect(header().string('Location', "/teams/$teamName"))
        when:
            ResultActions fetchResponse = mockMvcClient.get('/registration')
        then:
            fetchResponse
                    .andExpect(jsonPath('$[0].name', is(teamName)))
    }
}
