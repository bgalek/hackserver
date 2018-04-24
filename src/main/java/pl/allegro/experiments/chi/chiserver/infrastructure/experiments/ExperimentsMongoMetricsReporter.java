package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.MeterRegistry;

public class ExperimentsMongoMetricsReporter {
    private static final String ALL_EXPERIMENTS = "mongo.experiments.all";
    private static final String SINGLE_EXPERIMENT = "mongo.experiments.single";
    private static final String BAYESIAN_EXPERIMENT_STATISTICS_READ = "mongo.experiments.bayesian.read";
    private static final String BAYESIAN_EXPERIMENT_STATISTICS_WRITE = "mongo.experiments.bayesian.write";

    private final MeterRegistry metricRegistry;

    public ExperimentsMongoMetricsReporter(MeterRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    Timer timerAllExperiments() {
        return metricRegistry.timer(ALL_EXPERIMENTS);
    }

    Timer timerSingleExperiment() {
        return metricRegistry.timer(SINGLE_EXPERIMENT);
    }

    public Timer timerReadBayesianExperimentStatistics() {
        return metricRegistry.timer(BAYESIAN_EXPERIMENT_STATISTICS_READ);
    }

    public Timer timerWriteBayesianExperimentStatistics() {
        return metricRegistry.timer(BAYESIAN_EXPERIMENT_STATISTICS_WRITE);
    }
}
