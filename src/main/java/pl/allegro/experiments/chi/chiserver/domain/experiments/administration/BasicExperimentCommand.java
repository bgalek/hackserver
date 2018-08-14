package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;


import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications.Notificator;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class BasicExperimentCommand {

    private static final String EXPERIMENT_NOTIFICATION = "Experiment notification";
    private final ExperimentCommand experimentCommand;
    private final Notificator emailService;
    private final UserProvider userProvider;

    BasicExperimentCommand(ExperimentCommand experimentCommand, Notificator emailService, UserProvider userProvider) {
        this.experimentCommand = experimentCommand;
        this.emailService = emailService;
        this.userProvider = userProvider;
    }

    public void execute() {
        experimentCommand.execute();
        emailService.send(EXPERIMENT_NOTIFICATION,
                experimentCommand.getNotificationMessage() + authorMessage() + " at " + dateTimeMessage() + ". ");
    }

    private String dateTimeMessage() {
        return ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String authorMessage() {
        return "Changes were made by: " + userProvider.getCurrentUser().getName();
    }
}
