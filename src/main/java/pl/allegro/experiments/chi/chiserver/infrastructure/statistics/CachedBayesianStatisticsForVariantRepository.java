package pl.allegro.experiments.chi.chiserver.infrastructure.statistics;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatisticsForVariant;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianStatisticsForVariantRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CachedBayesianStatisticsForVariantRepository implements BayesianStatisticsForVariantRepository {

    private static final Logger logger = LoggerFactory.getLogger(CachedBayesianStatisticsForVariantRepository.class);

    private static final long REFRESH_RATE_IN_SECONDS = 3600;
    private Map<String, List<BayesianExperimentStatisticsForVariant>> experimentStatistics;
    private final BayesianStatisticsForVariantRepository delegate;
    private final ExperimentsRepository experimentsRepository;

    CachedBayesianStatisticsForVariantRepository(
            BayesianStatisticsForVariantRepository delegate,
            ExperimentsRepository experimentsRepository) {
        this.experimentStatistics = ImmutableMap.copyOf(new HashMap<>());
        this.delegate = delegate;
        this.experimentsRepository = experimentsRepository;
        refreshStatistics();
    }

    @Override
    public List<BayesianExperimentStatisticsForVariant> getLatestForAllVariants(String experimentId) {
        return experimentStatistics.getOrDefault(experimentId, ImmutableList.copyOf(Collections.emptyList()));
    }

    @Override
    public void save(BayesianExperimentStatisticsForVariant experimentStatistics) {
        delegate.save(experimentStatistics);
        refreshExperimentStatistics(experimentStatistics.getExperimentId());
    }

    @Scheduled(fixedDelay = REFRESH_RATE_IN_SECONDS * 1_000,
            initialDelay = REFRESH_RATE_IN_SECONDS * 1_000)
    private void refreshStatistics() {
        try {
            experimentsRepository.getAll()
                    .forEach(experimentDefinition ->
                            refreshExperimentStatistics(experimentDefinition.getId())
                    );
        } catch (Exception e) {
            logger.error("Bayesian statistics refresh for all experiments failed, ", e);
        }

    }

    synchronized private void refreshExperimentStatistics(String experimentId) {
        try {
            HashMap<String, List<BayesianExperimentStatisticsForVariant>> freshStatistics = new HashMap<>(experimentStatistics);
            freshStatistics.put(experimentId, delegate.getLatestForAllVariants(experimentId));
            experimentStatistics = ImmutableMap.copyOf(freshStatistics);
        } catch (Exception e) {
            logger.error(
                    "Bayesian statistics refresh for experiment " + experimentId + "failed ", e);
        }
    }
}
