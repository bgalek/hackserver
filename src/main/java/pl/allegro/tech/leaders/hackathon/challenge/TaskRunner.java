package pl.allegro.tech.leaders.hackathon.challenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeResult.ChallengeResultBuilder;
import pl.allegro.tech.leaders.hackathon.challenge.api.TeamClient;
import pl.allegro.tech.leaders.hackathon.registration.api.RegisteredTeam;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Clock;

class TaskRunner {
    private static final Logger logger = LoggerFactory.getLogger(TaskRunner.class);

    private final TeamClient teamClient;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    TaskRunner(ObjectMapper objectMapper, TeamClient teamClient, Clock clock) {
        this.objectMapper = objectMapper;
        this.teamClient = teamClient;
        this.clock = clock;
    }

    Mono<ChallengeResult> run(ChallengeDefinition challenge, TaskDefinition task, RegisteredTeam team) {
        final URI teamEndpoint = UriComponentsBuilder
                .newInstance()
                .scheme("https")
                .host(team.getRemoteAddress())
                .port(443)
                .path(challenge.getChallengeEndpoint())
                .queryParams(new LinkedMultiValueMap<>(task.getParameters()))
                .build(task.isParametersEncoded())
                .toUri();
        logger.info("running task of '{}' for team '{}', remote address: {}", challenge.getName(), team.getName(), teamEndpoint);
        final long start = clock.millis();
        return teamClient
                .execute(teamEndpoint)
                .onErrorResume(e -> Mono.just(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE)))
                .map(it -> score(it, challenge, task, team, start));
    }

    private ChallengeResult score(ResponseEntity<String> response, ChallengeDefinition challenge,
                                  TaskDefinition task, RegisteredTeam team, long start) {
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

        if (challenge.solutionType().getName().equals(String.class.getName())) {
            int score = calculateLatencyPenalty(task, response.getBody(), latency);
            return resultBuilder.buildSuccessResult(score);
        }

        Either<String, ?> solution = parse(response.getBody(), challenge.solutionType(), team);

        if (!solution.isRight()) {
            return resultBuilder.buildErrorResult(solution.left().get());
        }

        int score = calculateLatencyPenalty(task, solution.right().get(), latency);
        return resultBuilder.buildSuccessResult(score);
    }

    private int calculateLatencyPenalty(TaskDefinition task, Object o, long latency) {
        int score = task.scoreSolution(o);

//        No penalty in remote competition :)
//        if (task.getTaskScoring().getLatencyPenaltyFactor() > 0) {
//            score = score - (int) latency / task.getTaskScoring().getLatencyPenaltyFactor();
//        }
        logger.debug("latency {}, ignored", latency);
        return Math.max(score, 0);
    }

    /**
     * @return parsed solution or errorMessage
     */
    private <T> Either<String, T> parse(String body, Class<T> solutionType, RegisteredTeam team) {
        try {
            return Either.right(objectMapper.readValue(body, solutionType));
        } catch (Exception e) {
            logger.info("can't parse response from a team {} - {}", team.getName(), e.getMessage());
            return Either.left(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}

