package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;

import java.util.stream.Collectors;

public class EventDefinitionSaver {
    private final EventDefinitionRepository eventDefinitionRepository;
    private final ReadOnlyExperimentsRepository experimentsRepository;

    public EventDefinitionSaver(
            EventDefinitionRepository eventDefinitionRepository,
            ReadOnlyExperimentsRepository experimentsRepository) {
        Preconditions.checkNotNull(eventDefinitionRepository);
        Preconditions.checkNotNull(experimentsRepository);
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.experimentsRepository = experimentsRepository;
    }

    public void saveCurrentEventDefinitions() {
        eventDefinitionRepository.saveExperimentsEventDefinitions(
                experimentsRepository.getAll().stream()
                        .filter(experiment -> experiment.getDefinition().isPresent())
                        .map(experiment -> experiment.getDefinition().get())
                        .collect(Collectors.toList())
        );
    }
}
