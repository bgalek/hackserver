package pl.allegro.experiments.chi.chiserver.infrastructure.scorer;

import org.springframework.stereotype.Repository;

import java.util.Collections;

@Repository
public class MongoScorerContainerRepository {
    private final ScorerContainerCrudRepository scorerContainerCrudRepository;

    public MongoScorerContainerRepository(ScorerContainerCrudRepository scorerContainerCrudRepository) {
        this.scorerContainerCrudRepository = scorerContainerCrudRepository;
    }

    void save(ScorerContainer scorerContainer) {
        scorerContainerCrudRepository.save(scorerContainer);
    }

    ScorerContainer get() {
        return scorerContainerCrudRepository.findById(ScorerContainer.ID)
                .orElseGet(() -> new ScorerContainer(
                        ScorerContainer.ID,
                        Collections.emptyList(),
                        Collections.emptyList()));
    }
}
