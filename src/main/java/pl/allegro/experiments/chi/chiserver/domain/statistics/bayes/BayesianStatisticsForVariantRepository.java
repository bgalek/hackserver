package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import java.util.List;

public interface BayesianStatisticsForVariantRepository {
    List<BayesianExperimentStatisticsForVariant> getLatestForAllVariants(String experimentId, String device);

    void save(BayesianExperimentStatisticsForVariant experimentStatistics);
}
