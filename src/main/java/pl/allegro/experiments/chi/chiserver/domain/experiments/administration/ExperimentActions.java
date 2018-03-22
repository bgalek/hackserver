package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create.ExperimentCreationRequest;

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
        System.out.println( "req " + properties.toString());
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

}
