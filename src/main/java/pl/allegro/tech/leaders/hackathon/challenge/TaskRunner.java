package pl.allegro.tech.leaders.hackathon.challenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.timeout.TimeoutException;
import io.vavr.control.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeResult.ChallengeResultBuilder;
import pl.allegro.tech.leaders.hackathon.challenge.api.TeamClient;
import pl.allegro.tech.leaders.hackathon.registration.api.RegisteredTeam;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.time.Clock;
import java.util.Map.Entry;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;

class TaskRunner {
    private static final Logger logger = LoggerFactory.getLogger(TaskRunner.class);
    private final static long LATENCY_PENALTY_FOR_EACH_MILLIS = 200;

    private final TeamClient teamClient;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    TaskRunner(ObjectMapper objectMapper, TeamClient teamClient, Clock clock) {
        this.objectMapper = objectMapper;
        this.teamClient = teamClient;
        this.clock = clock;
    }

    Mono<ChallengeResult> run(ChallengeDefinition challenge, TaskDefinition task, RegisteredTeam team) {

        String teamEndpoint = "http://" + team.getRemoteAddress().getHostAddress() + ":8080" + challenge.getChallengeEndpoint();
        String taskParams = buildQueryParams(task);
        logger.info("running task of '{}' for team '{}', remote address: {}", challenge.getName(), team.getName(), teamEndpoint);

        final long start = clock.millis();

        Mono<ResponseEntity<String>> responseEntity = teamClient
                .execute(URI.create(teamEndpoint + taskParams))
                .onErrorResume(e -> {
                    if (e instanceof TimeoutException) {
                        return Mono.just(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));
                    } else {
                        // TODO: why not returning a result with 0 points and an error message?
                        return Mono.error(e);
                    }
                });

        return responseEntity.map(it -> score(it, challenge, task, team, start));
    }

    private String buildQueryParams(TaskDefinition task) {
        return task.getParams().entrySet().stream()
                .sorted(comparing(Entry::getKey))
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(joining("&", "?", ""));
    }

    private ChallengeResult score(
            ResponseEntity<String> response, ChallengeDefinition challenge,
            TaskDefinition task, RegisteredTeam team, long start) {
        // TODO: Add a policy to disable latency panelty for integration tests. Otherwise integration tests will flap.
        long latency = clock.millis() - start;
        ChallengeResultBuilder resultBuilder = ChallengeResult.builder(team.getId(), challenge.getId(), task.getName())
                .executedAt(clock.instant())
                .withResponse(response)
                .withLatency(latency);

        if (!response.getStatusCode().is2xxSuccessful()) {
            String errorMessage = "got HTTP status " + response.getStatusCode().value() + " from team '" + team.getName() + "', 2xx expected";
            logger.info(errorMessage);
            return resultBuilder.buildErrorResult(errorMessage);
        }

        Either<String, ?> solution = parse(response.getBody(), challenge.solutionType(), team);

        if (!solution.isRight()) {
            return resultBuilder.buildErrorResult(solution.left().get());
        }

        int score = calculateLatencyPenalty(task.scoreSolution(solution.right().get()), latency);
        return resultBuilder.buildSuccessResult(score);
    }

    private int calculateLatencyPenalty(int nominalScore, long latency) {
        if (nominalScore == 0) {
            return nominalScore;
        }

        int scoreWithPenalty = nominalScore - (int) latency / (int) LATENCY_PENALTY_FOR_EACH_MILLIS;

        if (scoreWithPenalty >= 0) {
            return scoreWithPenalty;
        }
        return 0;
    }

    /**
     * @return parsed solution or errorMessage
     */
    private <T> Either<String, T> parse(String body, Class<T> solutionType, RegisteredTeam team) {
        try {
            return Either.right(objectMapper.readValue(body, solutionType));
        } catch (IOException e) {
            logger.info("can't parse response from a team {}", team.getName(), e);
            return Either.left(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}

