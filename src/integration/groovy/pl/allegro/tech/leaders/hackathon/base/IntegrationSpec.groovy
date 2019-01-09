package pl.allegro.tech.leaders.hackathon.base

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder
import org.springframework.web.context.WebApplicationContext
import pl.allegro.tech.leaders.hackathon.HackathonServer
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup

@ContextConfiguration
@SpringBootTest(classes = [HackathonServer])
@CompileStatic
abstract class IntegrationSpec extends Specification implements DatabaseCleaner {
    @Autowired
    WebApplicationContext webApplicationContext

    @Shared
    MockMvcHttpClient mockMvcClient

    void setup() {
        ConfigurableMockMvcBuilder mockMvcBuilder = webAppContextSetup(webApplicationContext)
        MockMvc mockMvc = mockMvcBuilder.build()
        mockMvcClient = new MockMvcHttpClient(mockMvc)
    }
}
