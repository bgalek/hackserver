package pl.allegro.tech.leaders.hackathon.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.vavr.control.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeTaskDefinition;
import pl.allegro.tech.leaders.hackathon.registration.api.RegisteredTeam;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

@Service
public class TaskRunner {
    private static final Logger logger = LoggerFactory.getLogger(TaskRunner.class);

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final int clientTimeoutMillis;

    @Autowired
    public TaskRunner(ObjectMapper objectMapper, @Value("${client.timeout}") int clientTimeoutMillis) {
        this.objectMapper = objectMapper;
        this.clientTimeoutMillis = clientTimeoutMillis;
        this.webClient = initWebClient(clientTimeoutMillis);
    }

    public <T> Mono<TaskResult> run(ChallengeDefinition<T> challenge, ChallengeTaskDefinition<T> task,
                                    RegisteredTeam team) {

        String teamEndpoint = team.getUri() + challenge.getChallengeEndpoint();
        logger.info("running example task of '{}' for team '{}', remote address: {}", challenge.getName(), team.getName(), teamEndpoint);

        Mono<ResponseEntity<String>> responseEntity = webClient.get()
                .uri(uri(teamEndpoint))
                .exchange()
                .flatMap(response -> response.toEntity(String.class))
                .onErrorResume(e -> {
                    if (e instanceof TimeoutException) {
                        return Mono.just(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));
                    } else {
                        return Mono.error(e);
                    }
                });

        return responseEntity.map(it -> score(it, challenge, task, team));
    }

    private <T> TaskResult score(ResponseEntity<String> response, ChallengeDefinition<T> challenge,
                                 ChallengeTaskDefinition<T> task, RegisteredTeam team) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            String errorMessage = "got HTTP status "+response.getStatusCode().value()+" from team '"+team.getName()+"', 2xx expected";
            logger.info(errorMessage);
            return new FailedResult(response, errorMessage);
        }

        Either<String, T> solution = parse(response.getBody(), challenge.solutionType(), team);

        if(!solution.isRight()) {
            return new FailedResult(response, solution.left().get());
        }

        return new TaskResult(response, task.scoreSolution(solution.right().get()));
    }

    /**
     * @return parsed solution or errorMessage
     */
    private <T> Either<String, T> parse(String body, Class<T> solutionType, RegisteredTeam team) {
        try {
            return Either.right(objectMapper.readValue(body, solutionType));
        } catch (IOException e) {
            logger.info("can't parse response from a team {}", team.getName(), e.getMessage());
            return Either.left(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private URI uri(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private WebClient initWebClient(int clientTimeoutMillis) {
        HttpClient httpClient = HttpClient.create().tcpConfiguration(client ->
            client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, clientTimeoutMillis)
                  .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(clientTimeoutMillis, TimeUnit.MILLISECONDS))));

        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}

