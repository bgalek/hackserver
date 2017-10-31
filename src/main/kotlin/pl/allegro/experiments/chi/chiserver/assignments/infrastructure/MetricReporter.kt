package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import com.codahale.metrics.MetricRegistry

class MetricReporter(private val metricRegistry: MetricRegistry) {
    companion object {
        private val SAVED_METRIC_NAME = "chi.server.experiments.assignments.kafka.saved"
        private val FAILED_METRIC_NAME = "chi.server.experiments.assignments.kafka.failed"
        private val DROPPED_METRIC_NAME = "chi.server.experiments.assignments.kafka.dropped"
    }

    fun reportSaved(savedCount: Long) {
        metricRegistry.counter(SAVED_METRIC_NAME).inc(savedCount)
    }

    fun reportFailed(failedCount: Long) {
        metricRegistry.counter(FAILED_METRIC_NAME).inc(failedCount)
    }

    fun reportDropped(droppedCount: Long) {
        metricRegistry.counter(DROPPED_METRIC_NAME).inc(droppedCount)
    }
}