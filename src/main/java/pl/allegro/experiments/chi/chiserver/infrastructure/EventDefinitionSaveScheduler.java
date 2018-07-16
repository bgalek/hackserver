package pl.allegro.experiments.chi.chiserver.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinitionSaver;

public class EventDefinitionSaveScheduler {
    private static final Logger logger = LoggerFactory.getLogger(EventDefinitionSaveScheduler.class);
    private final EventDefinitionSaver eventDefinitionSaver;

    public EventDefinitionSaveScheduler(EventDefinitionSaver eventDefinitionSaver) {
        this.eventDefinitionSaver = eventDefinitionSaver;
    }

    @Scheduled(cron = "0 35 15 * * ?")
    public void save() {
        logger.info("Scheduling saveCurrentEventDefinitions");
        eventDefinitionSaver.saveCurrentEventDefinitions();
    }
}
