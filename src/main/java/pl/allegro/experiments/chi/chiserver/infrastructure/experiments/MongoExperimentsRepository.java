package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import com.codahale.metrics.Timer;
import org.javers.core.Javers;
import org.springframework.data.mongodb.core.MongoTemplate;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;

import java.util.List;
import java.util.stream.Collectors;

public class MongoExperimentsRepository implements ExperimentsRepository {
    private static final String COLLECTION = "experiments";
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
    public Experiment getExperiment(String id) {
        Timer.Context context = experimentsMongoMetricsReporter.timerSingleExperiment();
        Experiment experiment = mongoTemplate.findById(id, Experiment.class, COLLECTION);
        context.close();
        return experiment;
    }

    @Override
    public void delete(String experimentId) {
        Experiment experiment = getExperiment(experimentId);
        String username = userProvider.getCurrentUser().getName();
        javers.getLatestSnapshot(experimentId, Experiment.class).ifPresent(it ->
            javers.commitShallowDelete(username, experiment)
        );
        mongoTemplate.remove(getExperiment(experimentId), COLLECTION);
    }

    @Override
    public void save(Experiment experiment) {
        String username = userProvider.getCurrentUser().getName();
        javers.commit(username, experiment);
        mongoTemplate.save(experiment, COLLECTION);
    }

    @Override
    public List<Experiment> getAll() {
        Timer.Context context = experimentsMongoMetricsReporter.timerAllExperiments();
        List<Experiment> experiments = mongoTemplate.findAll(Experiment.class, COLLECTION);
        context.close();
        return experiments;
    }

    @Override
    public List<Experiment> assignable() {
        return getAll().stream().filter(Experiment::isAssignable).collect(Collectors.toList());
    }
}
