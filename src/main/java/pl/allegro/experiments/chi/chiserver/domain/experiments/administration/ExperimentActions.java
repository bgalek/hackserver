package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinition;

import java.util.List;

@Service
public class ExperimentActions {

    private final ExperimentsNotifications experimentsNotifications;

    @Autowired
    public ExperimentActions(ExperimentsNotifications experimentsNotifications) {
        this.experimentsNotifications = experimentsNotifications;
    }

    public void create(ExperimentCreationRequest experimentCreationRequest) {
        experimentsNotifications.createExperiment(experimentCreationRequest);
    }

    public void start(
            String experimentId,
            StartExperimentProperties properties) {
        experimentsNotifications.startExperiment(experimentId, properties);
    }

    public void prolong(
            String experimentId,
            ProlongExperimentProperties properties) {
        experimentsNotifications.prolongExperiment(experimentId, properties);
    }

    public void updateDescriptions(String experimentId, UpdateExperimentProperties properties) {
        experimentsNotifications.updateDescriptions(experimentId, properties);
    }

    public void updateVariants(String experimentId, UpdateVariantsProperties properties) {
        experimentsNotifications.updateVariants(experimentId, properties);
    }

    public void updateExperimentEventDefinitions(String experimentId, List<EventDefinition> eventDefinitions) {
        experimentsNotifications.updateExperimentEventDefinitions(experimentId, eventDefinitions);
    }

    public void makeExperimentFullOn(String experimentId, MakeExperimentFullOnProperties properties) {
        experimentsNotifications.makeExperimentFullOn(experimentId, properties);
    }

    public void stop(String experimentId) {
        experimentsNotifications.stopExperiment(experimentId);
    }

    public void pause(String experimentId) {
        experimentsNotifications.pauseExperiment(experimentId);
    }
    
    public void resume(String experimentId) {
        experimentsNotifications.resumeExperiment(experimentId);
    }

    public void delete(String experimentId) {
        experimentsNotifications.deleteExperiment(experimentId);
    }

    public void createExperimentGroup(ExperimentGroupCreationRequest experimentGroupCreationRequest) {
        experimentsNotifications.createExperimentGroup(experimentGroupCreationRequest);
    }

    public void createPairedExperiment(PairedExperimentCreationRequest pairedExperimentCreationRequest) {
        experimentsNotifications.createPairedExperiment(pairedExperimentCreationRequest);
    }
}
