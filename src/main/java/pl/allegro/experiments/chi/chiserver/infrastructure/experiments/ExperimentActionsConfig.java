package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentActions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create.CreateExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete.DeleteExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.pause.PauseExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong.ProlongExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.resume.ResumeExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start.StartExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.stop.StopExperimentCommandFactory;

@Configuration
class ExperimentActionsConfig {

    @Bean
    ExperimentActions experimentActions(
            CreateExperimentCommandFactory createExperimentCommandFactory,
            StartExperimentCommandFactory startExperimentCommandFactory,
            ProlongExperimentCommandFactory prolongExperimentCommandFactory,
            StopExperimentCommandFactory stopExperimentCommandFactory,
            PauseExperimentCommandFactory pauseExperimentCommandFactory,
            ResumeExperimentCommandFactory resumeExperimentCommandFactory,
            DeleteExperimentCommandFactory deleteExperimentCommandFactory) {
        return new ExperimentActions(
                createExperimentCommandFactory,
                startExperimentCommandFactory,
                prolongExperimentCommandFactory,
                stopExperimentCommandFactory,
                pauseExperimentCommandFactory,
                resumeExperimentCommandFactory,
                deleteExperimentCommandFactory);
    }
}
