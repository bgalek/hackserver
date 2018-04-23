package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import io.micrometer.core.instrument.Timer;
import org.javers.core.Javers;
import org.springframework.data.mongodb.core.MongoTemplate;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
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
    public Optional<Experiment> getExperiment(String id) {
        Timer timer = experimentsMongoMetricsReporter.timerSingleExperiment();

        ExperimentDefinition definition = timer.record(
            () -> mongoTemplate.findById(id, ExperimentDefinition.class, COLLECTION));
        return Optional.ofNullable(definition).map(ExperimentDefinition::toExperiment);
    }

    @Override
    public void delete(String experimentId) {
        ExperimentDefinition definition = getExperiment(experimentId)
                .flatMap(Experiment::getDefinition)
                .orElse(null);
        String username = userProvider.getCurrentUser().getName();
        javers.getLatestSnapshot(experimentId, ExperimentDefinition.class).ifPresent(it ->
            javers.commitShallowDelete(username, definition)
        );
        mongoTemplate.remove(getExperiment(experimentId)
                .flatMap(Experiment::getDefinition)
                .orElse(null), COLLECTION);
    }

    @Override
    public void save(ExperimentDefinition experimentDefinition) {
        String username = userProvider.getCurrentUser().getName();
        javers.commit(username, experimentDefinition);
        mongoTemplate.save(experimentDefinition, COLLECTION);
    }

    @Override
    public List<Experiment> getAll() {
        Timer timer = experimentsMongoMetricsReporter.timerAllExperiments();

        List<Experiment> experiments = timer.record(() ->
            mongoTemplate.findAll(ExperimentDefinition.class, COLLECTION)
                         .stream()
                         .map(ExperimentDefinition::toExperiment)
                         .collect(Collectors.toList())
        );
        return experiments;
    }

    @Override
    public List<Experiment> assignable() {
        return getAll().stream()
                .filter(Experiment::isAssignable)
                .collect(Collectors.toList());
    }
}
