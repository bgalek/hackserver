package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

public interface ExperimentCommand extends Command {

    String getNotificationMessage();

    String getExperimentId();
}
