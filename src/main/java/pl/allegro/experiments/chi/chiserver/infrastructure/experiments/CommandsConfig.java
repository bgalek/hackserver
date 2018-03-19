package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create.CreateExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete.DeleteExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.pause.PauseExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong.ProlongExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.resume.ResumeExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start.StartExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.stop.StopExperimentCommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.statistics.StatisticsRepository;

@Configuration
public class CommandsConfig {
    @Bean
    CreateExperimentCommandFactory createExperimentCommandFactory(
            ExperimentsRepository experimentsRepository,
            UserProvider userProvider) {
        return new CreateExperimentCommandFactory(experimentsRepository, userProvider);
    }

    @Bean
    DeleteExperimentCommandFactory deleteExperimentCommandFactory(
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository,
            StatisticsRepository statisticsRepository) {
        return new DeleteExperimentCommandFactory(
                experimentsRepository,
                permissionsAwareExperimentRepository,
                statisticsRepository);
    }

    @Bean
    ProlongExperimentCommandFactory prolongExperimentCommandFactory(
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository) {
        return new ProlongExperimentCommandFactory(experimentsRepository, permissionsAwareExperimentRepository);
    }

    @Bean
    StartExperimentCommandFactory startExperimentCommandFactory(
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository) {
        return new StartExperimentCommandFactory(experimentsRepository, permissionsAwareExperimentRepository);
    }

    @Bean
    StopExperimentCommandFactory stopExperimentCommandFactory(
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository) {
        return new StopExperimentCommandFactory(experimentsRepository, permissionsAwareExperimentRepository);
    }

    @Bean
    PauseExperimentCommandFactory pauseExperimentCommandFactory(
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository) {
        return new PauseExperimentCommandFactory(experimentsRepository, permissionsAwareExperimentRepository);
    }

    @Bean
    ResumeExperimentCommandFactory resumeExperimentCommandFactory(
            ExperimentsRepository experimentsRepository,
            PermissionsAwareExperimentRepository permissionsAwareExperimentRepository) {
        return new ResumeExperimentCommandFactory(experimentsRepository, permissionsAwareExperimentRepository);
    }
}
