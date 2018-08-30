package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import io.micrometer.core.instrument.Timer;
import org.apache.commons.collections.IteratorUtils;
import org.javers.core.Javers;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
class MongoExperimentsRepository implements ExperimentsRepository {
    private final ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter;
    private final Javers javers;
    private final UserProvider userProvider;
    private final ExperimentDefinitionCrudRepository experimentDefinitionCrudRepository;

    public MongoExperimentsRepository(ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter, Javers javers, UserProvider userProvider, ExperimentDefinitionCrudRepository experimentDefinitionCrudRepository) {
        this.experimentsMongoMetricsReporter = experimentsMongoMetricsReporter;
        this.javers = javers;
        this.userProvider = userProvider;
        this.experimentDefinitionCrudRepository = experimentDefinitionCrudRepository;
    }

    @Override
    public Optional<ExperimentDefinition> getExperiment(String id) {
        Timer timer = experimentsMongoMetricsReporter.timerSingleExperiment();
        Optional<ExperimentDefinition> experiment = timer.record(
            () -> experimentDefinitionCrudRepository.findById(id));
        return experiment;
    }

    @Override
    public void delete(String experimentId) {
        ExperimentDefinition definition = getExperiment(experimentId).orElse(null);
        String username = userProvider.getCurrentUser().getName();
        javers.getLatestSnapshot(experimentId, ExperimentDefinition.class).ifPresent(it ->
            javers.commitShallowDelete(username, definition)
        );
        if (definition != null) {
            experimentDefinitionCrudRepository.delete(definition);
        }
    }

    @Override
    public void save(ExperimentDefinition experimentDefinition) {
        String username = userProvider.getCurrentUser().getName();
        javers.commit(username, experimentDefinition);
        experimentDefinitionCrudRepository.save(experimentDefinition);
    }

    @Override
    public List<ExperimentDefinition> getAll() {
        Timer timer = experimentsMongoMetricsReporter.timerAllExperiments();
        return timer.record(() -> IteratorUtils.toList(experimentDefinitionCrudRepository.findAll().iterator()));
    }

    @Override
    public List<ExperimentDefinition> assignable() {
        return getAll().stream()
                .filter(ExperimentDefinition::isAssignable)
                .collect(Collectors.toList());
    }
}
