package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class BayesianStatisticsRepository {
    private static final Logger logger = LoggerFactory.getLogger(BayesianStatisticsRepository.class);

    private final BayesianStatisticsForVariantRepository bayesianStatisticsForVariantRepository;

    @Autowired
    public BayesianStatisticsRepository(BayesianStatisticsForVariantRepository bayesianStatisticsForVariantRepository) {
        this.bayesianStatisticsForVariantRepository = bayesianStatisticsForVariantRepository;
    }

    public Optional<BayesianExperimentStatistics> experimentStatistics(String experimentId, String device) {
        var stats = bayesianStatisticsForVariantRepository.getLatestForAllVariants(experimentId, device);

        if (stats.isEmpty()) {
            return Optional.empty();
        }

        if (!isValid(experimentId, device, stats)) {
            return Optional.empty();
        }

        return Optional.of(new BayesianExperimentStatistics(experimentId, stats.get(0).getToDate(), device, stats.stream()
                .map(it -> it.getData())
                .collect(Collectors.toList())));
    }

    private boolean isValid(String experimentId, String device, List<BayesianExperimentStatisticsForVariant> stats) {
        var distinctVariantNames = stats.stream().map(it -> it.getVariantName()).collect(Collectors.toSet());
        var distinctDates = stats.stream().map(it -> it.getToDate()).collect(Collectors.toSet());

        if (distinctVariantNames.size() != stats.size()) {
            logger.error("Corrupted bayesian statistics data for {} {}, variant names are not unique", experimentId, device);
            stats.forEach(it -> logger.error("- {} {} {} {}", it.getExperimentId(), it.getDevice(), it.getToDate(), it.getVariantName()));
            return false;
        }

        if (distinctDates.size() != 1) {
            logger.error("Corrupted bayesian statistics data for {} {}, toDate is not unique", experimentId, device);
            stats.forEach(it -> logger.error("- {} {} {} {}", it.getExperimentId(), it.getDevice(), it.getToDate(), it.getVariantName()));
            return false;
        }

        return true;
    }

}
