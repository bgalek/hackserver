package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.stream.Collectors;

public class EventDefinitionSaver {
    private final EventDefinitionRepository eventDefinitionRepository;
    private final ExperimentsRepository experimentsRepository;

    public EventDefinitionSaver(
            EventDefinitionRepository eventDefinitionRepository,
            ExperimentsRepository experimentsRepository) {
        Preconditions.checkNotNull(eventDefinitionRepository);
        Preconditions.checkNotNull(experimentsRepository);
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.experimentsRepository = experimentsRepository;
    }

    public void saveCurrentEventDefinitions() {
        List<ExperimentDefinition> experiments = experimentsRepository.getAll();
        eventDefinitionRepository.saveExperimentsEventDefinitions(experiments);
    }
}
