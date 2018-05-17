package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;

import java.util.Optional;

@Repository
public class BayesianChartsRepository {
    private static final Logger logger = LoggerFactory.getLogger(BayesianChartsRepository.class);

    private final BayesianStatisticsRepository bayesianStatisticsRepository;

    @Autowired
    public BayesianChartsRepository(BayesianStatisticsRepository bayesianStatisticsRepository) {
        this.bayesianStatisticsRepository = bayesianStatisticsRepository;
    }

    public Optional<BayesianVerticalEqualizer> getVerticalEqualizer(String experimentId, DeviceClass deviceClass) {
        logger.info("query for bayesian VerticalEqualizer, experimentId: {}, device: {} ", experimentId, deviceClass);
        return bayesianStatisticsRepository.experimentStatistics(experimentId, deviceClass.name())
              .map(BayesianVerticalEqualizer::new);
    }

    public Optional<BayesianHistograms> getHistograms(String experimentId, DeviceClass deviceClass) {
        logger.info("query for bayesian Histograms, experimentId: {}, device: {}, toDate: {} ", experimentId, deviceClass);
        return bayesianStatisticsRepository.experimentStatistics(experimentId, deviceClass.name())
                .map(BayesianHistograms::new);
    }
}
