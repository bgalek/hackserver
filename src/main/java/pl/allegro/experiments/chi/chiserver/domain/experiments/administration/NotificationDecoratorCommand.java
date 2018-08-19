package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications.Notification;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications.Notificator;

class NotificationDecoratorCommand implements Command {
    private final ExperimentCommand experimentCommand;
    private final Notificator notificator;
    private final UserProvider userProvider;

    NotificationDecoratorCommand(ExperimentCommand experimentCommand, Notificator notificator, UserProvider userProvider) {
        this.experimentCommand = experimentCommand;
        this.notificator = notificator;
        this.userProvider = userProvider;
    }

    public void execute() {
        experimentCommand.execute();
        notificator.send(
                new Notification(experimentCommand.getExperimentId(),
                                 experimentCommand.getNotificationMessage(),
                                 userProvider.getCurrentUser().getName()));
    }
}
