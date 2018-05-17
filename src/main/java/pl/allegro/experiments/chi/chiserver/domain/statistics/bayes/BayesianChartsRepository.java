package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Repository
public class BayesianChartsRepository {
    private static final Logger logger = LoggerFactory.getLogger(BayesianChartsRepository.class);

    private final BayesianStatisticsRepository bayesianStatisticsRepository;

    LoadingCache<StatsKey, Optional<BayesianExperimentStatistics>> statsCache = CacheBuilder.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<>() {
                        public Optional<BayesianExperimentStatistics> load(StatsKey key) {
                            logger.debug("loading BayesianExperimentStatistics from Mongo for experimentId: {}, device: {}",key.experimentId, key.deviceClass.name());
                            return bayesianStatisticsRepository.experimentStatistics(key.experimentId, key.deviceClass.name());
                        }
                    });

    @Autowired
    public BayesianChartsRepository(BayesianStatisticsRepository bayesianStatisticsRepository) {
        this.bayesianStatisticsRepository = bayesianStatisticsRepository;
    }

    public Optional<BayesianVerticalEqualizer> getVerticalEqualizer(String experimentId, DeviceClass deviceClass) {
        logger.debug("query for bayesian VerticalEqualizer, experimentId: {}, device: {}", experimentId, deviceClass);
        return statsFromCache(experimentId, deviceClass).map(BayesianVerticalEqualizer::new);
    }

    public Optional<BayesianHorizontalEqualizer> getHorizontalEqualizer(String experimentId, DeviceClass deviceClass) {
        logger.debug("query for bayesian HorizontalEqualizer, experimentId: {}, device: {}", experimentId, deviceClass);
        return getVerticalEqualizer(experimentId, deviceClass).map(BayesianHorizontalEqualizer::new);
    }

    public Optional<BayesianHistograms> getHistograms(String experimentId, DeviceClass deviceClass) {
        logger.debug("query for bayesian Histograms, experimentId: {}, device: {}", experimentId, deviceClass);
        return statsFromCache(experimentId, deviceClass).map(BayesianHistograms::new);
    }

    private Optional<BayesianExperimentStatistics> statsFromCache(String experimentId, DeviceClass deviceClass) {
        try {
            return statsCache.get(new StatsKey(experimentId, deviceClass));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static class StatsKey {
        final String experimentId;
        final DeviceClass deviceClass;

        public StatsKey(String experimentId, DeviceClass deviceClass) {
            this.experimentId = experimentId;
            this.deviceClass = deviceClass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StatsKey statsKey = (StatsKey) o;
            return Objects.equals(
                    experimentId, statsKey.experimentId) &&
                    deviceClass == statsKey.deviceClass;
        }

        @Override
        public int hashCode() {
            return Objects.hash(experimentId, deviceClass);
        }
    }
}
