package pl.allegro.tech.leaders.hackathon.challenge

import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeActivationResult
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeDefinition
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeDetails
import pl.allegro.tech.leaders.hackathon.challenge.base.InMemorySolutionClient
import pl.allegro.tech.leaders.hackathon.challenge.base.UpdatableFixedClock
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import spock.lang.Specification

import static java.util.Comparator.comparing
import static pl.allegro.tech.leaders.hackathon.challenge.base.ReactorValueExtractor.extract
import static pl.allegro.tech.leaders.hackathon.challenge.Challenge.ChallengeState

abstract class ChallengeSpec extends Specification {
    InMemorySolutionClient solutionClient = new InMemorySolutionClient()
    UpdatableFixedClock clock = UpdatableFixedClock.defaultClock()
    ChallengeFacade facade = new ChallengeConfiguration()
            .challengeFacade(clock, new InMemoryChallengeStateRepository())

    protected void registerChallengeDefinitions(ChallengeDefinition... definitions) {
        extract(facade.registerChallengeDefinitions(definitions.toList()))
    }

    protected ChallengeDetails getActiveChallenge(String id) {
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
    Flux<ChallengeState> findActive() {
        return this.findAll()
            .filter { it.active }
            .sort(comparing({ ChallengeState c -> c.activatedAt }))
    }

    private Flux<ChallengeState> findAll() {
        return Flux.fromIterable(store.values())
    }
}
