package pl.allegro.experiments.chi.chiserver.domain.statistics;

import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatistics;

import java.util.Optional;

public interface BayesianStatisticsRepository {
    Optional<BayesianExperimentStatistics> experimentStatistics(String experimentId, String toDate, String device);

    void save(BayesianExperimentStatistics experimentStatistics);
}
