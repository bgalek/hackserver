package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications.Notificator;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class BasicExperimentCommand {
    private static final Logger logger = LoggerFactory.getLogger(BasicExperimentCommand.class);

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

        try {
            emailService.send(EXPERIMENT_NOTIFICATION, experimentCommand.getNotificationMessage() + authorMessage() + " at " + dateTimeMessage() + ". ");
        } catch (Exception e) {
            logger.error("Error while sending email notification", e);
        }
    }

    private String dateTimeMessage() {
        return ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String authorMessage() {
        return "Changes were made by: " + userProvider.getCurrentUser().getName();
    }
}
