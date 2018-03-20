package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ReadOnlyExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.InMemoryExperimentsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FileBasedExperimentsRepository implements ReadOnlyExperimentsRepository {
    private static final Logger logger = LoggerFactory.getLogger(FileBasedExperimentsRepository.class);
    private final HttpContentLoader dataLoader;
    private final Gson jsonConverter;
    private final InMemoryExperimentsRepository inMemoryExperimentsRepository;
    private String jsonUrl;

    public FileBasedExperimentsRepository(
            String jsonUrl,
            HttpContentLoader dataLoader,
            Gson jsonConverter,
            List<Experiment> initialState) {
        if (initialState == null) {
            initialState = new ArrayList<>();
        }
        this.dataLoader = dataLoader;
        this.jsonConverter = jsonConverter;
        this.inMemoryExperimentsRepository = new InMemoryExperimentsRepository(initialState);
        this.jsonUrl = jsonUrl;
        secureRefresh();
    }


    public void secureRefresh() {
        try {
            refresh();
        } catch (Exception e) {
            logger.error("Error while loading experiments file.", e);
        }
    }

    private void refresh() {
        String data = dataLoader.loadFromHttp(jsonUrl);

        if (data.isEmpty()) {
            logger.error("refresh failed, dataLoader has returned empty String");
            return;
        }

        List<Experiment> freshExperiments;
        try {
            freshExperiments = jsonConverter.fromJson(data, (new TypeToken<List<Experiment>>() {}).getType());
        } catch (IllegalArgumentException e) {
            logger.error("refresh failed, malformed experiments definition in JSON: " + e.getMessage());
            return;
        }
        Set<String> currentExperimentIds = inMemoryExperimentsRepository.experimentIds();
        Set<String> freshExperimentsIds = freshExperiments.stream()
                .map(it -> it.getId())
                .collect(Collectors.toSet());
        Set<String> removedExperiments = Sets.difference(currentExperimentIds, freshExperimentsIds);
        freshExperiments.stream().forEach(it -> inMemoryExperimentsRepository.save(it));
        removedExperiments.stream().forEach(it -> inMemoryExperimentsRepository.remove(it));
        logger.debug(freshExperiments.size() + " experiment(s) successfully loaded");
    }

    @Override
    public Experiment getExperiment(String id) {
        return inMemoryExperimentsRepository.getExperiment(id);
    }

    @Override
    public List<Experiment> getAll() {
        return inMemoryExperimentsRepository.getAll();
    }

    @Override
    public List<Experiment> assignable() {
        return inMemoryExperimentsRepository.assignable();
    }
}
