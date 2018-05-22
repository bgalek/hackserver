package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import org.javers.core.Javers;
import org.springframework.data.mongodb.core.MongoTemplate;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;

import java.util.Optional;

public class MongoExperimentGroupRepository implements ExperimentGroupRepository {

    private static final String COLLECTION = "experimentGroups";
    private final MongoTemplate mongoTemplate;
    private final Javers javers;
    private final UserProvider userProvider;

    public MongoExperimentGroupRepository(
            MongoTemplate mongoTemplate,
            Javers javers,
            UserProvider userProvider) {
        this.mongoTemplate = mongoTemplate;
        this.javers = javers;
        this.userProvider = userProvider;
    }

    @Override
    public void save(ExperimentGroup experimentGroup) {
        String username = userProvider.getCurrentUser().getName();
        javers.commit(username, experimentGroup);
        mongoTemplate.save(experimentGroup, COLLECTION);
    }

    @Override
    public Optional<ExperimentGroup> get(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, ExperimentGroup.class, COLLECTION));
    }

    @Override
    public boolean exists(String id) {
        return get(id).isPresent();
    }
}
