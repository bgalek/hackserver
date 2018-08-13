package pl.allegro.experiments.chi.chiserver.application.commands

import org.apache.commons.collections4.map.HashedMap
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentRepository
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicExperimentStatistics
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.StatisticsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.InMemoryExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsTestConfig
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MutableUserProvider

@Configuration
class CommandTestConfig extends ExperimentsTestConfig {
    @Primary
    @Bean
    PermissionsAwareExperimentRepository permissionsAwareExperimentRepository(
            InMemoryExperimentsRepository experimentsRepository,
            MutableUserProvider mutableUserProvider) {
        new PermissionsAwareExperimentRepository(experimentsRepository, mutableUserProvider);
    }

    @Primary
    @Bean
    InMemoryStatisticsRepository statisticsRepository() {
        new InMemoryStatisticsRepository()
    }
}

class InMemoryStatisticsRepository implements StatisticsRepository {
    Map<String, Boolean> statistics = new HashedMap<>()

    void statisticsExist(String experimentId) {
        statistics[experimentId] = true
    }

    void statisticsDoNotExist(String experimentId) {
        statistics[experimentId] = false
    }

    @Override
    Optional<ClassicExperimentStatistics> getExperimentStatistics(String experimentId, String device) {
        Optional.empty()
    }

    @Override
    boolean hasAnyStatistics(String experimentId) {
        statistics.getOrDefault(experimentId, false)
    }
}



