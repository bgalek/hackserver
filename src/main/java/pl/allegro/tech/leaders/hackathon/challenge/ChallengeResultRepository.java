package pl.allegro.tech.leaders.hackathon.challenge;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.Repository;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeResult.ChallengeResultId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

interface ChallengeResultRepository extends Repository<ChallengeResult, ChallengeResultId> {
    Mono<ChallengeResult> save(ChallengeResult challengeResult);

    @Query("{ '_id.challengeId' : ?0 }")
    Flux<ChallengeResult> findByChallengeId(String challengeId);

    @Query("{ '_id.teamId' : ?0 }")
    Flux<ChallengeResult> findByTeamId(String teamId);
}
