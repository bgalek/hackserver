package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Timer
import com.github.slugify.Slugify
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import pl.allegro.experiments.chi.chiserver.domain.interactions.Interaction

class ExperimentsMongoMetricsReporter(private val metricRegistry: MetricRegistry) {

    private val ALL_EXPERIMENTS = "mongo.experiments.all"

    private val SINGLE_EXPERIMENT = "mongo.experiments.single"

    fun timerAllExperiments(): Timer.Context {
        return metricRegistry.timer(ALL_EXPERIMENTS).time()
    }

    fun timerSingleExperiment(): Timer.Context {
        return metricRegistry.timer(SINGLE_EXPERIMENT).time()
    }
}
