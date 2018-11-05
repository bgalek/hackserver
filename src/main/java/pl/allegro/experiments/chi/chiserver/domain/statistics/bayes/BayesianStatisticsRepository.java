package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class BayesianStatisticsRepository {
    private static final Logger logger = LoggerFactory.getLogger(BayesianStatisticsRepository.class);

    private final BayesianStatisticsForVariantRepository bayesianStatisticsForVariantRepository;

    @Autowired
    public BayesianStatisticsRepository(BayesianStatisticsForVariantRepository bayesianStatisticsForVariantRepository) {
        this.bayesianStatisticsForVariantRepository = bayesianStatisticsForVariantRepository;
    }

    public List<BayesianExperimentStatistics> experimentStatistics(String experimentId) {
        var stats = bayesianStatisticsForVariantRepository.getLatestForAllVariants(experimentId);

        if (!isValid(experimentId, stats)) {
            return Collections.emptyList();
        }

        Map<DeviceClass, List<BayesianExperimentStatisticsForVariant>> statsPerDevice = new HashMap<>();
        for (BayesianExperimentStatisticsForVariant deviceStats: stats) {
            if (!statsPerDevice.containsKey(deviceStats.getDevice())) {
                statsPerDevice.put(deviceStats.getDevice(), new LinkedList<>());
            }
            statsPerDevice.get(deviceStats.getDevice()).add(deviceStats);
        }
        var result = statsPerDevice.keySet().stream()
                .map(deviceClass ->
                        new BayesianExperimentStatistics(
                                experimentId,
                                statsPerDevice.get(deviceClass).get(0).getToDate(),
                                deviceClass.toString(),
                                statsPerDevice.get(deviceClass).stream()
                                    .map(it -> it.getData())
                                    .collect(Collectors.toList())
                        )
                ).collect(Collectors.toList());

        return result;
    }

    private boolean isValid(String experimentId, List<BayesianExperimentStatisticsForVariant> stats) {
        var distinctVariantNames = stats.stream().map(it -> it.getVariantName()).collect(Collectors.toSet());
        var distinctDeviceClasses = stats.stream().map(it -> it.getDevice()).collect(Collectors.toSet());
        var distinctDates = stats.stream().map(it -> it.getToDate()).collect(Collectors.toSet());

        if (distinctVariantNames.size() * distinctDeviceClasses.size() != stats.size()) {
            logger.error("Corrupted bayesian statistics data for {}, variant names are not unique", experimentId);
            stats.forEach(it -> logger.error("- {} {} {} {}", it.getExperimentId(), it.getDevice(), it.getToDate(), it.getVariantName()));
            return false;
        }

        if (distinctDates.size() != 1) {
            logger.error("Corrupted bayesian statistics data for {}, toDate is not unique", experimentId);
            stats.forEach(it -> logger.error("- {} {} {} {}", it.getExperimentId(), it.getDevice(), it.getToDate(), it.getVariantName()));
            return false;
        }

        return true;
    }

}
