package pl.allegro.experiments.chi.chiserver.domain.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public class BayesianEqualizersRepository {
    private final BayesianStatisticsRepository bayesianStatisticsRepository;

    @Autowired
    public BayesianEqualizersRepository(BayesianStatisticsRepository bayesianStatisticsRepository) {
        this.bayesianStatisticsRepository = bayesianStatisticsRepository;
    }

    public Optional<BayesianVerticalEqualizer> getVerticalEqualizer(String experimentId, DeviceClass deviceClass, LocalDate toDate) {
        return bayesianStatisticsRepository.experimentStatistics(experimentId, deviceClass.name(), toDate.toString())
              .map(BayesianVerticalEqualizer::new);
    }
}
