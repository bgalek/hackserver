package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create.CreateExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create.ExperimentCreationRequest;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete.DeleteExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.pause.PauseExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong.ProlongExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong.ProlongExperimentProperties;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.resume.ResumeExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start.StartExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start.StartExperimentProperties;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.stop.StopExperimentCommandFactory;

public class ExperimentActions {
    private final CreateExperimentCommandFactory createExperimentCommandFactory;
    private final StartExperimentCommandFactory startExperimentCommandFactory;
    private final ProlongExperimentCommandFactory prolongExperimentCommandFactory;
    private final StopExperimentCommandFactory stopExperimentCommandFactory;
    private final PauseExperimentCommandFactory pauseExperimentCommandFactory;
    private final ResumeExperimentCommandFactory resumeExperimentCommandFactory;
    private final DeleteExperimentCommandFactory deleteExperimentCommandFactory;

    public ExperimentActions(
            CreateExperimentCommandFactory createExperimentCommandFactory,
            StartExperimentCommandFactory startExperimentCommandFactory,
            ProlongExperimentCommandFactory prolongExperimentCommandFactory,
            StopExperimentCommandFactory stopExperimentCommandFactory,
            PauseExperimentCommandFactory pauseExperimentCommandFactory,
            ResumeExperimentCommandFactory resumeExperimentCommandFactory,
            DeleteExperimentCommandFactory deleteExperimentCommandFactory) {
        this.createExperimentCommandFactory = createExperimentCommandFactory;
        this.startExperimentCommandFactory = startExperimentCommandFactory;
        this.prolongExperimentCommandFactory = prolongExperimentCommandFactory;
        this.stopExperimentCommandFactory = stopExperimentCommandFactory;
        this.pauseExperimentCommandFactory = pauseExperimentCommandFactory;
        this.resumeExperimentCommandFactory = resumeExperimentCommandFactory;
        this.deleteExperimentCommandFactory = deleteExperimentCommandFactory;
    }

    public void create(ExperimentCreationRequest experimentCreationRequest) {
        createExperimentCommandFactory.createExperimentCommand(experimentCreationRequest)
                .execute();
    }

    public void start(
            String experimentId,
            StartExperimentProperties properties) {
        startExperimentCommandFactory.startExperimentCommand(experimentId, properties)
                .execute();
    }

    public void prolong(
            String experimentId,
            ProlongExperimentProperties properties) {
        prolongExperimentCommandFactory.prolongExperimentCommand(experimentId, properties)
                .execute();
    }

    public void stop(String experimentId) {
        stopExperimentCommandFactory.stopExperimentCommand(experimentId)
                .execute();
    }

    public void pause(String experimentId) {
        pauseExperimentCommandFactory.pauseExperimentCommand(experimentId)
                .execute();
    }
    
    public void resume(String experimentId) {
        resumeExperimentCommandFactory.resumeExperimentCommand(experimentId)
                .execute();
    }

    public void delete(String experimentId) {
        deleteExperimentCommandFactory.deleteExperimentCommand(experimentId)
                .execute();
    }
}
