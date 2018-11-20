package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import avro.shaded.com.google.common.collect.Lists;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Repository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentTag;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentTagRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MongoExperimentTagRepository implements ExperimentTagRepository {
    private final ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter;
    private final ExperimentTagCrudRepository experimentTagCrudRepository;

    public MongoExperimentTagRepository(
            ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter,
            ExperimentTagCrudRepository experimentTagCrudRepository) {
        this.experimentsMongoMetricsReporter = experimentsMongoMetricsReporter;
        this.experimentTagCrudRepository = experimentTagCrudRepository;
    }

    @Override
    public void save(ExperimentTag experimentTag) {
        Timer timer = experimentsMongoMetricsReporter.timerWriteExperimentTag();
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

    @Override
    public boolean tagsExist(List<String> experimentTagIds) {
        return all().stream()
                .map(it -> it.getId())
                .collect(Collectors.toSet())
                .containsAll(experimentTagIds);
    }

}