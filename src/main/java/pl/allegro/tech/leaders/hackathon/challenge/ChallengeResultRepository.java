package pl.allegro.tech.leaders.hackathon.challenge;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.Repository;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeResult.ChallengeResultId;
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeResultsUpdatedEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class ChallengeResultRepository {

    private final PersistenceChallengeResultRepository persistenceChallengeResultRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    ChallengeResultRepository(PersistenceChallengeResultRepository persistenceChallengeResultRepository,
                              ApplicationEventPublisher applicationEventPublisher) {
        this.persistenceChallengeResultRepository = persistenceChallengeResultRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    Mono<ChallengeResult> save(ChallengeResult challengeResult) {
        return persistenceChallengeResultRepository.save(challengeResult)
                .doOnSuccess(it -> applicationEventPublisher
                        .publishEvent(new ChallengeResultsUpdatedEvent(it.toTaskResult())));
    }

    Flux<ChallengeResult> findByChallengeId(String challengeId) {
        return persistenceChallengeResultRepository.findByChallengeId(challengeId);
    }

    Flux<ChallengeResult> findByTeamId(String teamId) {
        return persistenceChallengeResultRepository.findByTeamId(teamId);
    }
}

interface PersistenceChallengeResultRepository extends Repository<ChallengeResult, ChallengeResultId> {
    Mono<ChallengeResult> save(ChallengeResult challengeResult);

    @Query("{ '_id.challengeId' : ?0 }")
    Flux<ChallengeResult> findByChallengeId(String challengeId);

    @Query("{ '_id.teamId' : ?0 }")
    Flux<ChallengeResult> findByTeamId(String teamId);
}