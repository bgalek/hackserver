package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.MeterRegistry;

public class ExperimentsMongoMetricsReporter {
    private static final String ALL_EXPERIMENTS = "mongo.experiments.all";
    private static final String SINGLE_EXPERIMENT = "mongo.experiments.single";
    private static final String BAYESIAN_EXPERIMENT_STATISTICS_READ = "mongo.experiments.bayesian.read";
    private static final String BAYESIAN_EXPERIMENT_STATISTICS_WRITE = "mongo.experiments.bayesian.write";
    private static final String ALL_EXPERIMENT_GROUPS = "mongo.experiments.groups.all";
    private static final String SAVE_EXPERIMENT_GROUP = "mongo.experiments.groups.save";

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

    public Timer timerAllExperimentGroups() {
        return metricRegistry.timer(ALL_EXPERIMENT_GROUPS);
    }

    public Timer timerSaveExperimentGroup() {
        return metricRegistry.timer(SAVE_EXPERIMENT_GROUP);
    }
}
