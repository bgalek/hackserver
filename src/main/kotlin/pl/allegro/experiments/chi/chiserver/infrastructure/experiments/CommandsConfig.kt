package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create.CreateExperimentCommandFactory
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete.DeleteExperimentCommandFactory
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.pause.PauseExperimentCommandFactory
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong.ProlongExperimentCommandFactory
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.resume.ResumeExperimentCommandFactory
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start.StartExperimentCommandFactory
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.stop.StopExperimentCommandFactory
import pl.allegro.experiments.chi.chiserver.domain.statistics.StatisticsRepository

@Configuration
class CommandsConfig {

    @Bean
    fun createExperimentCommandFactory(
            experimentsRepository: ExperimentsRepository,
            userProvider: UserProvider): CreateExperimentCommandFactory {
        return CreateExperimentCommandFactory(experimentsRepository, userProvider)
    }

    @Bean
    fun deleteExperimentCommandFactory(
            experimentsRepository: ExperimentsRepository,
            permissionsAwareExperimentRepository: PermissionsAwareExperimentRepository,
            statisticsRepository: StatisticsRepository): DeleteExperimentCommandFactory {
        return DeleteExperimentCommandFactory(
                experimentsRepository,
                permissionsAwareExperimentRepository,
                statisticsRepository)
    }

    @Bean
    fun prolongExperimentCommandFactory(
            experimentsRepository: ExperimentsRepository,
            permissionsAwareExperimentRepository: PermissionsAwareExperimentRepository): ProlongExperimentCommandFactory {
        return ProlongExperimentCommandFactory(experimentsRepository, permissionsAwareExperimentRepository)
    }

    @Bean
    fun startExperimentCommandFactory(
            experimentsRepository: ExperimentsRepository,
            permissionsAwareExperimentRepository: PermissionsAwareExperimentRepository): StartExperimentCommandFactory {
        return StartExperimentCommandFactory(experimentsRepository, permissionsAwareExperimentRepository)
    }

    @Bean
    fun stopExperimentCommandFactory(
            experimentsRepository: ExperimentsRepository,
            permissionsAwareExperimentRepository: PermissionsAwareExperimentRepository): StopExperimentCommandFactory {
        return StopExperimentCommandFactory(experimentsRepository, permissionsAwareExperimentRepository)
    }

    @Bean
    fun pauseExperimentCommandFactory(
            experimentsRepository: ExperimentsRepository,
            permissionsAwareExperimentRepository: PermissionsAwareExperimentRepository): PauseExperimentCommandFactory {
        return PauseExperimentCommandFactory(experimentsRepository, permissionsAwareExperimentRepository)
    }

    @Bean
    fun resumeExperimentCommandFactory(
            experimentsRepository: ExperimentsRepository,
            permissionsAwareExperimentRepository: PermissionsAwareExperimentRepository): ResumeExperimentCommandFactory {
        return ResumeExperimentCommandFactory(experimentsRepository, permissionsAwareExperimentRepository)
    }
}
