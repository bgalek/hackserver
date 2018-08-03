package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinition;

import java.util.List;

@Service
public class ExperimentActions {

    private final CommandFactory commandFactory;

    @Autowired
    public ExperimentActions(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    public void create(ExperimentCreationRequest experimentCreationRequest) {
        commandFactory.createExperimentCommand(experimentCreationRequest).execute();
    }

    public void start(
            String experimentId,
            StartExperimentProperties properties) {
        commandFactory.startExperimentCommand(experimentId, properties).execute();
    }

    public void prolong(
            String experimentId,
            ProlongExperimentProperties properties) {
        commandFactory.prolongExperimentCommand(experimentId, properties).execute();
    }

    public void updateDescriptions(String experimentId, UpdateExperimentProperties properties) {
        commandFactory.updateDescriptionsCommand(experimentId, properties).execute();
    }

    public void updateVariants(String experimentId, UpdateVariantsProperties properties) {
        commandFactory.updateVariantsCommand(experimentId, properties).execute();
    }

    public void updateExperimentEventDefinitions(String experimentId, List<EventDefinition> eventDefinitions) {
        commandFactory.updateExperimentEventDefinitionsCommand(experimentId, eventDefinitions).execute();
    }

    public void makeExperimentFullOn(String experimentId, MakeExperimentFullOnProperties properties) {
        commandFactory.makeExperimentFullOnCommand(experimentId, properties).execute();
    }

    public void stop(String experimentId) {
        commandFactory.stopExperimentCommand(experimentId).execute();
    }

    public void pause(String experimentId) {
        commandFactory.pauseExperimentCommand(experimentId).execute();
    }
    
    public void resume(String experimentId) {
        commandFactory.resumeExperimentCommand(experimentId).execute();
    }

    public void delete(String experimentId) {
        commandFactory.deleteExperimentCommand(experimentId).execute();
    }

    public void createExperimentGroup(ExperimentGroupCreationRequest experimentGroupCreationRequest) {
        commandFactory.createExperimentGroupCommand(experimentGroupCreationRequest).execute();
    }

    public void createPairedExperiment(PairedExperimentCreationRequest pairedExperimentCreationRequest) {
        commandFactory.createPairedExperimentCommand(pairedExperimentCreationRequest).execute();
    }
}
