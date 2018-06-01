package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import org.javers.core.Javers;
import org.springframework.data.mongodb.core.MongoTemplate;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;

import java.util.List;
import java.util.Optional;

class MongoExperimentGroupRepository implements ExperimentGroupRepository {

    private static final String COLLECTION = "experimentGroups";
    private final MongoTemplate mongoTemplate;
    private final Javers javers;
    private final UserProvider userProvider;
    private final ExperimentsMongoMetricsReporter metrics;

    MongoExperimentGroupRepository(
            MongoTemplate mongoTemplate,
            Javers javers,
            UserProvider userProvider,
            ExperimentsMongoMetricsReporter metrics) {
        this.mongoTemplate = mongoTemplate;
        this.javers = javers;
        this.userProvider = userProvider;
        this.metrics = metrics;
    }

    @Override
    public void save(ExperimentGroup experimentGroup) {
        metrics.timerSaveExperimentGroup().record(() -> {
            String username = userProvider.getCurrentUser().getName();
            javers.commit(username, experimentGroup);
            mongoTemplate.save(experimentGroup, COLLECTION);
        });
    }

    @Override
    public Optional<ExperimentGroup> get(String id) {
        throw new UnsupportedOperationException("MongoExperimentGroupRepository.get not implemented");
    }

    @Override
    public boolean exists(String id) {
        throw new UnsupportedOperationException("MongoExperimentGroupRepository.exists not implemented");
    }

    @Override
    public boolean experimentInGroup(String experimentId) {
        throw new UnsupportedOperationException("MongoExperimentGroupRepository.experimentInGroup not implemented");
    }

    @Override
    public List<ExperimentGroup> all() {
        return metrics.timerAllExperimentGroups().record(() ->
                mongoTemplate.findAll(ExperimentGroup.class, COLLECTION));
    }

    @Override
    public Optional<ExperimentGroup> getExperimentGroup(String experimentId) {
        throw new UnsupportedOperationException("MongoExperimentGroupRepository.getExperimentGroup not implemented");
    }
}
