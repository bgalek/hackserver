package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import avro.shaded.com.google.common.collect.Lists;
import io.micrometer.core.instrument.Timer;
import org.javers.core.Javers;
import org.springframework.stereotype.Repository;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentTag;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentTagRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class MongoExperimentTagRepository implements ExperimentTagRepository {
    private final ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter;
    private final Javers javers;
    private final UserProvider userProvider;
    private final ExperimentTagCrudRepository experimentTagCrudRepository;

    public MongoExperimentTagRepository(
            ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter,
            Javers javers,
            UserProvider userProvider,
            ExperimentTagCrudRepository experimentTagCrudRepository) {
        this.experimentsMongoMetricsReporter = experimentsMongoMetricsReporter;
        this.javers = javers;
        this.userProvider = userProvider;
        this.experimentTagCrudRepository = experimentTagCrudRepository;
    }

    @Override
    public void save(ExperimentTag experimentTag) {
        Timer timer = experimentsMongoMetricsReporter.timerWriteExperimentTag();
        String username = userProvider.getCurrentUser().getName();
        javers.commit(username, experimentTag);
        timer.record(() -> experimentTagCrudRepository.save(experimentTag));
    }

    @Override
    public Optional<ExperimentTag> get(String experimentTagId) {
        Timer timer = experimentsMongoMetricsReporter.timerReadExperimentTag();
        return timer.record(() -> experimentTagCrudRepository.findById(experimentTagId));
    }

    @Override
    public List<ExperimentTag> all() {
        Timer timer = experimentsMongoMetricsReporter.timerAllExperimentTags();
        return timer.record(() -> Lists.newArrayList(experimentTagCrudRepository.findAll()));
    }
}
