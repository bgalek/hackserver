package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create.CreateExperimentCommandFactory
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete.DeleteExperimentCommandFactory
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong.ProlongExperimentCommandFactory
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
            permissionsAwareExperimentGetter: PermissionsAwareExperimentGetter,
            statisticsRepository: StatisticsRepository): DeleteExperimentCommandFactory {
        return DeleteExperimentCommandFactory(
                experimentsRepository,
                permissionsAwareExperimentGetter,
                statisticsRepository)
    }

    @Bean
    fun prolongExperimentCommandFactory(
            experimentsRepository: ExperimentsRepository,
            permissionsAwareExperimentGetter: PermissionsAwareExperimentGetter): ProlongExperimentCommandFactory {
        return ProlongExperimentCommandFactory(experimentsRepository, permissionsAwareExperimentGetter)
    }

    @Bean
    fun startExperimentCommandFactory(
            experimentsRepository: ExperimentsRepository,
            permissionsAwareExperimentGetter: PermissionsAwareExperimentGetter): StartExperimentCommandFactory {
        return StartExperimentCommandFactory(experimentsRepository, permissionsAwareExperimentGetter)
    }

    @Bean
    fun stopExperimentCommandFactory(
            experimentsRepository: ExperimentsRepository,
            permissionsAwareExperimentGetter: PermissionsAwareExperimentGetter): StopExperimentCommandFactory {
        return StopExperimentCommandFactory(experimentsRepository, permissionsAwareExperimentGetter)
    }
}