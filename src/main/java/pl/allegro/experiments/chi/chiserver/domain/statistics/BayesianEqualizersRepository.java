package pl.allegro.experiments.chi.chiserver.domain.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;

@Repository
public class BayesianEqualizersRepository {
    private final BayesianStatisticsRepository bayesianStatisticsRepository;

    @Autowired
    public BayesianEqualizersRepository(BayesianStatisticsRepository bayesianStatisticsRepository) {
        this.bayesianStatisticsRepository = bayesianStatisticsRepository;
    }

    BayesianHorizontalEqualizer getHorizontalEqualizer(ExperimentDefinition experiment) {
        return null;
    }
}
