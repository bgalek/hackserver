package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import org.javers.core.Javers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentActions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.audit.Audit;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create.CreateExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete.DeleteExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.pause.PauseExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong.ProlongExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.resume.ResumeExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start.StartExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.stop.StopExperimentCommandFactory;

import java.time.ZoneId;

@Configuration
class AuditConfig {

    @Bean
    Audit audit(Javers javers) {
        return new Audit(javers, ZoneId.systemDefault());
    }
}
