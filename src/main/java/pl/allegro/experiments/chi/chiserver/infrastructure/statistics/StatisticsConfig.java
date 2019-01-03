package pl.allegro.experiments.chi.chiserver.infrastructure.statistics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianStatisticsForVariantRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicStatisticsForVariantMetricRepository;

import javax.annotation.PostConstruct;

@Configuration
class StatisticsConfig {
    private static final String BAYESIAN_STATS_COUNT_METRIC =  "statistics.bayesian.count-by-experiment";
    private static final String CLASSIC_STATS_COUNT_METRIC =  "statistics.classic.count-by-experiment";

    @Autowired
    private BayesianStatisticsForVariantRepository bayesianRepository;

    @Autowired
    private ClassicStatisticsForVariantMetricRepository classicRepository;

    @Autowired
    private MeterRegistry meterRegistry;

    @PostConstruct
    public void registerGauges() {
        meterRegistry.gauge(CLASSIC_STATS_COUNT_METRIC, classicRepository,
                r -> r.countNumberExperimentsWithStats());
        meterRegistry.gauge(BAYESIAN_STATS_COUNT_METRIC, bayesianRepository,
                r -> r.countNumberExperimentsWithStats());
    }
}

