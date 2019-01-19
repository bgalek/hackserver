package pl.allegro.tech.leaders.hackathon.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeTaskDefinition;
import pl.allegro.tech.leaders.hackathon.registration.Team;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class TaskRunner {
    private static final Logger logger = LoggerFactory.getLogger(TaskRunner.class);

    private final WebClient webClient = WebClient.create();
    private final ObjectMapper objectMapper= new ObjectMapper();

    public <T> Mono<TaskResult> run(ChallengeDefinition<T> challenge, ChallengeTaskDefinition<T> task, Team team) {

        String teamEndpoint = team.getHttpRemoteAddr() + challenge.getChallengeEndpoint();
        logger.info("running example task of '{}' for team '{}', remote address: {}", challenge.getName(), team.getName(), teamEndpoint);

        Mono<String> responseBody =  webClient.get()
                .uri(uri(teamEndpoint))
                .retrieve()
                .bodyToMono(String.class);

        return responseBody
                .map(body -> parse(body, challenge.solutionType()))
                .map(solution -> task.scoreSolution(solution))
                .map(score -> new TaskResult("", 200, 1, score)); //TODO status & latency
    }

    private <T> T parse(String body, Class<T> solutionType) {
        try {
            return objectMapper.readValue(body, solutionType);
        } catch (IOException e) {
            //TODO add exception handling
            throw new RuntimeException(e);
        }
    }

    private URI uri(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}

