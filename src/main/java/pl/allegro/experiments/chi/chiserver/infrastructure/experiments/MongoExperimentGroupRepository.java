package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;

import java.util.List;

interface MongoExperimentGroupRepository extends CrudRepository<ExperimentGroup, String> {
    ExperimentGroup save(ExperimentGroup toSave);

    List<ExperimentGroup> findAll();
}