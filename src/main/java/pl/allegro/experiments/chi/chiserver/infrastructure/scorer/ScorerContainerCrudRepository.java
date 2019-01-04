package pl.allegro.experiments.chi.chiserver.infrastructure.scorer;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ScorerContainerCrudRepository extends CrudRepository<ScorerContainer, String>  {

    @Override
    <S extends ScorerContainer> S save(S entity);

    @Override
    Optional<ScorerContainer> findById(String s);
}
