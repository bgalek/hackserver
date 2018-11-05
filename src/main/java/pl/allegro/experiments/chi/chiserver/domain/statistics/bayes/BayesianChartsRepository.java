package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class BayesianChartsRepository {
    private static final Logger logger = LoggerFactory.getLogger(BayesianChartsRepository.class);

    private final BayesianStatisticsRepository bayesianStatisticsRepository;

    @Autowired
    public BayesianChartsRepository(BayesianStatisticsRepository bayesianStatisticsRepository) {
        this.bayesianStatisticsRepository = bayesianStatisticsRepository;
    }

    public List<BayesianVerticalEqualizer> getVerticalEqualizer(String experimentId) {
        logger.debug("query for bayesian VerticalEqualizer, experimentId: {}", experimentId);
        return bayesianStatisticsRepository.experimentStatistics(experimentId).stream()
                .map(BayesianVerticalEqualizer::new)
                .collect(Collectors.toList());
    }

    public List<BayesianHorizontalEqualizer> getHorizontalEqualizer(String experimentId) {
        logger.debug("query for bayesian HorizontalEqualizer, experimentId: {}", experimentId);
        return getVerticalEqualizer(experimentId).stream()
                .map(BayesianHorizontalEqualizer::new)
                .collect(Collectors.toList());
    }

    public List<BayesianHistograms> getHistograms(String experimentId) {
        logger.debug("query for bayesian Histograms, experimentId: {}", experimentId);
        return bayesianStatisticsRepository.experimentStatistics(experimentId).stream()
                .map(BayesianHistograms::new)
                .collect(Collectors.toList());
    }

}
