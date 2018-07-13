package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinitionRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.infrastructure.interactions.LoggerInteractionRepository;

import java.util.List;
import java.util.logging.Logger;

public class LoggerEventDefinitionRepository implements EventDefinitionRepository {
    public static final Logger logger = Logger.getLogger(LoggerInteractionRepository.class.getName());

    @Override
    public void saveExperimentsEventDefinitions(List<ExperimentDefinition> experimentDefinition) {
        logger.info("saving experiments event definitions");
    }
}
