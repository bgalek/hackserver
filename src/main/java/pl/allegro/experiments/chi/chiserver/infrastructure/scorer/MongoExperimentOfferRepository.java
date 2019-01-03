package pl.allegro.experiments.chi.chiserver.infrastructure.scorer;

import org.springframework.stereotype.Repository;

import java.util.Collections;

@Repository
public class MongoExperimentOfferRepository {
    private final ExperimentOfferCrudRepository experimentOfferCrudRepository;

    public MongoExperimentOfferRepository(ExperimentOfferCrudRepository experimentOfferCrudRepository) {
        this.experimentOfferCrudRepository = experimentOfferCrudRepository;
    }

    void save(ExperimentOffer experimentOffer) {
        experimentOfferCrudRepository.save(experimentOffer);
    }

    ExperimentOffer get() {
        return experimentOfferCrudRepository.findById(ExperimentOffer.ID)
                .orElseGet(() -> new ExperimentOffer(
                        ExperimentOffer.ID,
                        Collections.emptyList(),
                        Collections.emptyList()));
    }
}
