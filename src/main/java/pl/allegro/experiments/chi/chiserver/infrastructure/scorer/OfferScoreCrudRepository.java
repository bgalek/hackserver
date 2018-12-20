package pl.allegro.experiments.chi.chiserver.infrastructure.scorer;

import org.springframework.data.repository.CrudRepository;
import pl.allegro.experiments.chi.chiserver.domain.scorer.OfferScore;

public interface OfferScoreCrudRepository extends CrudRepository<OfferScore, String> {

    @Override
    <S extends OfferScore> Iterable<S> saveAll(Iterable<S> entities);

    @Override
    void deleteAll();

    @Override
    Iterable<OfferScore> findAll();
}
