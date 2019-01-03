package pl.allegro.experiments.chi.chiserver.infrastructure.scorer;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ExperimentOfferCrudRepository extends CrudRepository<ExperimentOffer, String>  {

    @Override
    <S extends ExperimentOffer> S save(S entity);

    @Override
    Optional<ExperimentOffer> findById(String s);
}
