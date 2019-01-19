package pl.allegro.tech.leaders.hackathon.challenge

import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeActivationResult
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

    protected List<ChallengeDetails> getActiveChallenges() {
        return extract(facade.getActiveChallenges())
    }

    protected ChallengeActivationResult activeChallenge(String id) {
        return extract(facade.activateChallenge(id))
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
    Flux<ChallengeState> findActivated() {
        return this.findAll()
            .filter { it.active }
            .sort(comparing({ ChallengeState c -> c.activatedAt }))
    }

    Flux<ChallengeState> findAll() {
        return Flux.fromIterable(store.values())
    }

    @Override
    Mono<Void> deleteAll() {
        store.clear()
    }
}