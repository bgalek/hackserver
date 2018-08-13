package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications.Notificator;

import java.util.List;

@Service
public class ExperimentsNotifications {

    private static final String EXPERIMENT_NOTIFICATION = "Experiment notification";
    private final Notificator emailService;
    private final CommandFactory commandFactory;

    @Autowired
    public ExperimentsNotifications(Notificator emailService, CommandFactory commandFactory) {
        this.emailService = emailService;
        this.commandFactory = commandFactory;
    }

    void createExperiment(ExperimentCreationRequest experimentCreationRequest) {
        CreateExperimentCommand experimentCommand = commandFactory.createExperimentCommand(experimentCreationRequest);
        experimentCommand.execute();
        emailService.send(EXPERIMENT_NOTIFICATION, experimentCommand.getNotificationMessage(experimentCreationRequest.getId()));
    }

    void startExperiment(
            String experimentId,
            StartExperimentProperties properties) {
        StartExperimentCommand experimentCommand = commandFactory.startExperimentCommand(experimentId, properties);
        experimentCommand.execute();
        emailService.send(EXPERIMENT_NOTIFICATION, experimentCommand.getNotificationMessage(experimentId));
    }

    void prolongExperiment(
            String experimentId,
            ProlongExperimentProperties properties) {
        ProlongExperimentCommand experimentCommand = commandFactory.prolongExperimentCommand(experimentId, properties);
        experimentCommand.execute();
        emailService.send(EXPERIMENT_NOTIFICATION, experimentCommand.getNotificationMessage(experimentId, properties));
    }

    void updateDescriptions(String experimentId, UpdateExperimentProperties properties) {
        UpdateDescriptionsCommand experimentCommand = commandFactory.updateDescriptionsCommand(experimentId, properties);
        experimentCommand.execute();
        emailService.send(EXPERIMENT_NOTIFICATION, experimentCommand.getNotificationMessage(experimentId));
    }

    void updateVariants(String experimentId, UpdateVariantsProperties properties) {
        UpdateVariantsCommand experimentCommand = commandFactory.updateVariantsCommand(experimentId, properties);
        experimentCommand.execute();
        emailService.send(EXPERIMENT_NOTIFICATION, experimentCommand.getNotificationMessage(experimentId));
    }

    void updateExperimentEventDefinitions(String experimentId, List<EventDefinition> eventDefinitions) {
        UpdateExperimentEventDefinitionsCommand experimentCommand = commandFactory.updateExperimentEventDefinitionsCommand(experimentId, eventDefinitions);
        experimentCommand.execute();
        emailService.send(EXPERIMENT_NOTIFICATION, experimentCommand.getNotificationMessage(experimentId));
    }

    void stopExperiment(String experimentId) {
        StopExperimentCommand experimentCommand = commandFactory.stopExperimentCommand(experimentId);
        experimentCommand.execute();
        emailService.send(EXPERIMENT_NOTIFICATION, experimentCommand.getNotificationMessage(experimentId));
    }

    void pauseExperiment(String experimentId) {
        PauseExperimentCommand experimentCommand = commandFactory.pauseExperimentCommand(experimentId);
        experimentCommand.execute();
        emailService.send(EXPERIMENT_NOTIFICATION, experimentCommand.getNotificationMessage(experimentId));
    }

    void resumeExperiment(String experimentId) {
        ResumeExperimentCommand experimentCommand = commandFactory.resumeExperimentCommand(experimentId);
        experimentCommand.execute();
        emailService.send(EXPERIMENT_NOTIFICATION, experimentCommand.getNotificationMessage(experimentId));
    }

    void deleteExperiment(String experimentId) {
        DeleteExperimentCommand experimentCommand = commandFactory.deleteExperimentCommand(experimentId);
        experimentCommand.execute();
        emailService.send(EXPERIMENT_NOTIFICATION, experimentCommand.getNotificationMessage(experimentId));
    }

    void createExperimentGroup(ExperimentGroupCreationRequest experimentGroupCreationRequest) {
        CreateExperimentGroupCommand experimentCommand = commandFactory.createExperimentGroupCommand(experimentGroupCreationRequest);
        experimentCommand.execute();
        emailService.send(EXPERIMENT_NOTIFICATION, experimentCommand.getNotificationMessage(experimentGroupCreationRequest.getExperiments()));
    }

    void createPairedExperiment(PairedExperimentCreationRequest pairedExperimentCreationRequest) {
        CreatePairedExperimentCommand experimentCommand = commandFactory.createPairedExperimentCommand(pairedExperimentCreationRequest);
        experimentCommand.execute();
        emailService.send(EXPERIMENT_NOTIFICATION, experimentCommand.getNotificationMessage(pairedExperimentCreationRequest));
    }

    void makeExperimentFullOn(String experimentId, MakeExperimentFullOnProperties properties) {
        MakeExperimentFullOnCommand experimentCommand = commandFactory.makeExperimentFullOnCommand(experimentId, properties);
        experimentCommand.execute();
        emailService.send(EXPERIMENT_NOTIFICATION, experimentCommand.getNotificationMessage(experimentId, properties));
    }
}
