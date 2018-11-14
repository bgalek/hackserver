package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class ExperimentsMongoMetricsReporter {
    private static final String ALL_EXPERIMENTS = "mongo.experiments.all";
    private static final String SINGLE_EXPERIMENT = "mongo.experiments.single";
    private static final String BAYESIAN_EXPERIMENT_STATISTICS_READ = "mongo.experiments.bayesian.read";
    private static final String BAYESIAN_EXPERIMENT_STATISTICS_WRITE = "mongo.experiments.bayesian.write";
    private static final String CLASSIC_EXPERIMENT_STATISTICS_READ = "mongo.experiments.classic.read";
    private static final String CLASSIC_EXPERIMENT_STATISTICS_WRITE = "mongo.experiments.classic.write";
    private static final String ALL_EXPERIMENT_GROUPS = "mongo.experiments.groups.all";
    private static final String SAVE_EXPERIMENT_GROUP = "mongo.experiments.groups.save";
    private static final String WRITE_EXPERIMENT_TAG = "mongo.experiments.tags.write";
    private static final String READ_EXPERIMENT_TAG = "mongo.experiments.tags.read";

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

    public Timer timerReadClassicExperimentStatistics() {
        return metricRegistry.timer(CLASSIC_EXPERIMENT_STATISTICS_READ);
    }

    public Timer timerWriteClassicExperimentStatistics() {
        return metricRegistry.timer(CLASSIC_EXPERIMENT_STATISTICS_WRITE);
    }

    public Timer timerWriteExperimentTag() {
        return metricRegistry.timer(WRITE_EXPERIMENT_TAG);
    }

    public Timer timerReadExperimentTag() {
        return metricRegistry.timer(READ_EXPERIMENT_TAG);
    }

    public Timer timerAllExperimentGroups() {
        return metricRegistry.timer(ALL_EXPERIMENT_GROUPS);
    }

    public Timer timerSaveExperimentGroup() {
        return metricRegistry.timer(SAVE_EXPERIMENT_GROUP);
    }
}
