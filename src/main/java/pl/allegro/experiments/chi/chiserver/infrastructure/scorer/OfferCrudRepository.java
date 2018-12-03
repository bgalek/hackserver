package pl.allegro.experiments.chi.chiserver.infrastructure.scorer;

import org.springframework.data.repository.CrudRepository;
import pl.allegro.experiments.chi.chiserver.domain.scorer.Offer;

public interface OfferCrudRepository extends CrudRepository<Offer, String> {

    @Override
    <S extends Offer> Iterable<S> saveAll(Iterable<S> entities);

    @Override
    void deleteAll();

    @Override
    Iterable<Offer> findAll();
}
