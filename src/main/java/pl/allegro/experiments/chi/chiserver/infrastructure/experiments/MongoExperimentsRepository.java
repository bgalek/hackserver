package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import io.micrometer.core.instrument.Timer;
import org.javers.core.Javers;
import org.springframework.data.mongodb.core.MongoTemplate;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MongoExperimentsRepository implements ExperimentsRepository {
    private static final String COLLECTION = "experimentDefinitions";
    private final MongoTemplate mongoTemplate;
    private final ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter;
    private final Javers javers;
    private final UserProvider userProvider;

    public MongoExperimentsRepository(
            MongoTemplate mongoTemplate,
            ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter,
            Javers javers,
            UserProvider userProvider) {
        this.mongoTemplate = mongoTemplate;
        this.experimentsMongoMetricsReporter = experimentsMongoMetricsReporter;
        this.javers = javers;
        this.userProvider = userProvider;
    }

    @Override
    public Optional<ExperimentDefinition> getExperiment(String id) {
        Timer timer = experimentsMongoMetricsReporter.timerSingleExperiment();
        ExperimentDefinition experiment = timer.record(
            () -> mongoTemplate.findById(id, ExperimentDefinition.class, COLLECTION));
        return Optional.ofNullable(experiment);
    }

    @Override
    public void delete(String experimentId) {
        ExperimentDefinition definition = getExperiment(experimentId).orElse(null);
        String username = userProvider.getCurrentUser().getName();
        javers.getLatestSnapshot(experimentId, ExperimentDefinition.class).ifPresent(it ->
            javers.commitShallowDelete(username, definition)
        );
        if (definition != null) {
            mongoTemplate.remove(definition, COLLECTION);
        }
    }

    @Override
    public void save(ExperimentDefinition experimentDefinition) {
        String username = userProvider.getCurrentUser().getName();
        javers.commit(username, experimentDefinition);
        mongoTemplate.save(experimentDefinition, COLLECTION);
    }

    @Override
    public List<ExperimentDefinition> getAll() {
        Timer timer = experimentsMongoMetricsReporter.timerAllExperiments();
        return timer.record(() ->
            mongoTemplate.findAll(ExperimentDefinition.class, COLLECTION));
    }

    @Override
    public List<ExperimentDefinition> assignable() {
        return getAll().stream()
                .filter(ExperimentDefinition::isAssignable)
                .collect(Collectors.toList());
    }
}
