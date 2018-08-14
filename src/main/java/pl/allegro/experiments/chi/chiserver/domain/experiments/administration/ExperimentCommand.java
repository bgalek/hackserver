package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

public interface ExperimentCommand {

    void execute();

    String getNotificationMessage();
}
