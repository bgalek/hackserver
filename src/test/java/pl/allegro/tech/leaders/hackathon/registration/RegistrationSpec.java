package pl.allegro.tech.leaders.hackathon.registration;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.allegro.tech.leaders.hackathon.HackathonServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HackathonServer.class)
class RegistrationSpec {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should add Team to the Registered Teams registry")
    void addingTeam() throws Exception {
        // given
        mockMvc.perform(MockMvcRequestBuilders.get("/registration")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andDo(result -> assertEquals(result.getResponse().getContentAsString(), "[]"));

        // when
        String randomTeamName = UUID.randomUUID().toString();
        String content = String.format("{\"name\":\"%s\"}", randomTeamName);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(content)).andReturn();

        // then
        int status = result.getResponse().getStatus();
        assertEquals(HttpStatus.CREATED.value(), status);
        assertNotNull(result.getResponse());
        assertEquals(String.format("/teams/%s", randomTeamName), result.getResponse().getHeader("Location"));

        // and
        result = mockMvc.perform(MockMvcRequestBuilders.get("/registration")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        // then
        assertEquals(result.getResponse().getContentAsString(), String.format("[{\"name\":\"%s\"}]", randomTeamName));
    }

    @Configuration
    static class ContextConfiguration {
        @Bean
        public RegistrationService registrationService() {
            TeamRepository teamRepository = new TeamRepository() {
                private Map<String, Team> storage = new HashMap<>();

                @Override
                public void save(Team team) {
                    storage.put(team.getName(), team);
                }

                @Override
                public List<Team> findAll() {
                    return new ArrayList<>(storage.values());
                }
            };
            return new RegistrationService(teamRepository, new SimpleMeterRegistry(), new RegistrationEvents(new SimpMessagingTemplate((message, timeout) -> true)));
        }
    }
}

