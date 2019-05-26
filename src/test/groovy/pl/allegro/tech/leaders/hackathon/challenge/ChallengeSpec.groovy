package pl.allegro.tech.leaders.hackathon.challenge

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.util.UriComponentsBuilder
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeActivationResult
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeDetails
import pl.allegro.tech.leaders.hackathon.challenge.api.TaskResult
import pl.allegro.tech.leaders.hackathon.challenge.api.TeamClient
import pl.allegro.tech.leaders.hackathon.challenge.base.SampleResponse
import pl.allegro.tech.leaders.hackathon.challenge.base.UpdatableFixedClock
import pl.allegro.tech.leaders.hackathon.registration.RegistrationFacade
import pl.allegro.tech.leaders.hackathon.registration.api.RegisteredTeam
import pl.allegro.tech.leaders.hackathon.scores.api.ScoreRegistry
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import spock.lang.Specification

import static org.springframework.http.HttpStatus.NOT_FOUND
import static pl.allegro.tech.leaders.hackathon.challenge.Challenge.ChallengeState
import static pl.allegro.tech.leaders.hackathon.challenge.ChallengeResult.ChallengeResultId
import static pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition.TaskWithFixedResult
import static pl.allegro.tech.leaders.hackathon.challenge.base.JsonMapper.toJson
import static pl.allegro.tech.leaders.hackathon.challenge.base.ReactorValueExtractor.extract
import static pl.allegro.tech.leaders.hackathon.challenge.base.UpdatableFixedClock.defaultClock
import static pl.allegro.tech.leaders.hackathon.configuration.ObjectMapperProvider.objectMapper

abstract class ChallengeSpec extends Specification {
    InMemoryTeamClient teamClient = new InMemoryTeamClient()
    UpdatableFixedClock clock = defaultClock()
    ObjectMapper objectMapper = objectMapper()
    RegistrationFacade registrationFacade = Stub()
    ScoreRegistry scoreRegistry = Stub() {
        updateScores(_) >> { Flux<TaskResult> results -> results }
    }

    ChallengeFacade facade = new ChallengeConfiguration()
            .challengeFacade(
            clock,
            objectMapper,
            new InMemoryApplicationEventPublisher(),
            teamClient,
            new InMemoryChallengeStateRepository(),
            new InMemoryChallengeResultRepository(),
            registrationFacade,
            scoreRegistry)

    protected void registerChallengeDefinitions(ChallengeDefinition... definitions) {
        extract(facade.registerChallengeDefinitions(definitions.toList()))
    }

    protected Challenge getActiveChallenge(String id) {
        return extract(facade.getActiveChallenge(id))
    }

    protected List<ChallengeDetails> getActiveChallenges() {
        return extract(facade.getActiveChallenges())
    }

    protected ChallengeActivationResult deactivateChallenge(String id) {
        return extract(facade.deactivateChallenge(id))
    }

    protected ChallengeActivationResult activateChallenge(String id) {
        return extract(facade.activateChallenge(id))
    }

    protected List<ChallengeActivationResult> activeChallenges(String... id) {
        Flux<ChallengeActivationResult> results = Flux.fromIterable(id.toList())
                .flatMap({ facade.activateChallenge(it) })
        return extract(results)
    }
}

class InMemoryTeamClient implements TeamClient {
    private final Map<URI, Mono<ResponseEntity<String>>> store = new HashMap<>()
    private final Map<URI, Integer> counters = new HashMap<>()
    private Mono<ResponseEntity<String>> defaultResponse = null

    @Override
    Mono<ResponseEntity<String>> execute(URI uri) {
        incrementRequestCounter(uri)
        Mono<ResponseEntity<String>> result = store.getOrDefault(uri, defaultResponse)
        if (result == null) {
            return Mono.just(new ResponseEntity("No mapping found for: " + uri, NOT_FOUND))
        }
        return result
    }

    private void incrementRequestCounter(URI uri) {
        int previous = counters.getOrDefault(uri, 0)
        counters.put(uri, previous + 1)
    }

    int requestCount(RegisteredTeam team) {
        String teamUri = buildUri(team)
        return counters.entrySet()
                .findAll { it.key.toString().startsWith(teamUri) }
                .inject(0) { result, i -> result + i.value }
    }

    void recordResponse(String uri, String successBody) {
        recordResponse(uri, ResponseEntity.ok(successBody))
    }

    void recordResponse(String uri, ResponseEntity<String> response) {
        store.put(URI.create(uri), Mono.just(response))
    }

    void recordResponse(ResponseEntity<String> response) {
        defaultResponse = Mono.just(response)
    }

    void recordIncorrectResponses(RegisteredTeam team, ChallengeDefinition challenge) {
        challenge.getTasks()
                .each { recordIncorrectResponse(team, challenge, it as TaskWithFixedResult) }
    }

    void recordCorrectResponses(RegisteredTeam team, ChallengeDefinition challenge) {
        challenge.getTasks()
                .each { recordCorrectResponse(team, challenge, it as TaskWithFixedResult) }
    }

    void recordCorrectResponse(RegisteredTeam team, ChallengeDefinition challenge, TaskWithFixedResult task) {
        recordTeamResponse(team, challenge, task, toJson(task.getExpectedSolution()))
    }

    void recordIncorrectResponse(RegisteredTeam team, ChallengeDefinition challenge, TaskWithFixedResult task) {
        recordTeamResponse(team, challenge, task, toJson(new SampleResponse("NOT_VALID")))
    }

    void recordTeamResponse(RegisteredTeam team, ChallengeDefinition challenge, TaskWithFixedResult task, String response) {
        String uri = buildUri(team, challenge, task)
        recordResponse(uri, response)
    }

    private static String buildUri(RegisteredTeam team, ChallengeDefinition challenge, TaskWithFixedResult task) {
        return UriComponentsBuilder
                .newInstance()
                .scheme('http')
                .host(team.remoteAddress.getAddress().getHostAddress())
                .port(team.getRemoteAddress().getPort())
                .path(challenge.challengeEndpoint)
                .queryParams(new LinkedMultiValueMap<>(task.getParameters().collectEntries { key, value -> [key, [value]] }))
                .build()
                .toString()
    }

    private static String buildUri(RegisteredTeam team) {
        return UriComponentsBuilder
                .newInstance()
                .scheme('http')
                .host(team.remoteAddress.getAddress().getHostAddress())
                .port(team.getRemoteAddress().getPort()).build()
                .toString()
    }
}

class InMemoryChallengeStateRepository implements ChallengeStateRepository {
    private final Map<String, ChallengeState> store = new HashMap<>()

    @Override
    Mono<ChallengeState> save(ChallengeState state) {
        store.put(state.id, state)
        return Mono.just(state)
    }

    @Override
    Mono<ChallengeState> findById(String id) {
        return Mono.justOrEmpty(store.get(id))
    }

    @Override
    Flux<ChallengeState> findAll() {
        return Flux.fromIterable(store.values())
    }
}

class InMemoryApplicationEventPublisher implements ApplicationEventPublisher {
    List<Object> events = []

    @Override
    void publishEvent(ApplicationEvent event) {
        events.add(event)
    }

    @Override
    void publishEvent(Object event) {
        events.add(event)
    }
}

class InMemoryChallengeResultRepository implements ChallengeResultRepository {
    private final Map<ChallengeResultId, ChallengeResult> store = new HashMap<>()

    @Override
    Mono<ChallengeResult> save(ChallengeResult result) {
        store.put(result.id, result)
        return Mono.just(result)
    }

    @Override
    Flux<ChallengeResult> findByChallengeId(String challengeId) {
        List<ChallengeResult> filtered = store.values()
                .findAll { it.id.challengeId == challengeId }
        return Flux.fromIterable(filtered)
    }

    @Override
    Flux<ChallengeResult> findByTeamId(String teamId) {
        List<ChallengeResult> filtered = store.values()
                .findAll { it.id.teamId == teamId }
        return Flux.fromIterable(filtered)
    }
}
