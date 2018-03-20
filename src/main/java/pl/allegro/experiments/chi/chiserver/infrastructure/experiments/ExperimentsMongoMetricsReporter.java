package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

public class ExperimentsMongoMetricsReporter {
    private static final String ALL_EXPERIMENTS = "mongo.experiments.all";
    private static final String SINGLE_EXPERIMENT = "mongo.experiments.single";

    private final MetricRegistry metricRegistry;

    public ExperimentsMongoMetricsReporter(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    public Timer.Context timerAllExperiments() {
        return metricRegistry.timer(ALL_EXPERIMENTS).time();
    }

    public Timer.Context timerSingleExperiment() {
        return metricRegistry.timer(SINGLE_EXPERIMENT).time();
    }
}
