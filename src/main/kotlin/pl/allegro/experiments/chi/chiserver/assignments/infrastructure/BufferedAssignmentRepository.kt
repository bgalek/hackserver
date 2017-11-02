package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import com.codahale.metrics.MetricRegistry
import pl.allegro.experiments.chi.chiserver.assignments.Assignment
import pl.allegro.experiments.chi.chiserver.assignments.AssignmentRepository
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

class BufferedAssignmentRepository(
        private val metricRegistry: MetricRegistry,
        private val buffer: AssignmentBuffer,
        private val repository: AssignmentRepository): AssignmentRepository {

    companion object {
        private val SAVED_METRIC_NAME = "chi.server.experiments.assignments.kafka.saved"
        private val FAILED_METRIC_NAME = "chi.server.experiments.assignments.kafka.failed"
        private val logger = Logger.getLogger(BufferedAssignmentRepository::class.java.name)
    }

    override fun save(assignment: Assignment) {
        buffer.add(assignment)
    }

    fun flush() {
        val start = System.nanoTime()
        val assignments = buffer.flush()
        logger.info("Flushing ${assignments.size} assignments from buffer")
        val failedCounter = saveFromBuffer(assignments)
        reportSaved(assignments.size - failedCounter)
        reportFailed(failedCounter)
        val duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)
        logger.info("Flushed ${assignments.size}, took $duration ms")
    }

    private fun saveFromBuffer(assignments: List<Assignment>): Long {
        var failedCounter: Long = 0
        assignments.stream().forEach { assignment ->
            try {
                repository.save(assignment)
            } catch (e: CouldNotSendMessageToKafkaError) {
                logger.warning("Saving assignment failed, $e")
                failedCounter++
            }
        }
        return failedCounter
    }

    private fun reportSaved(savedCount: Long) {
        metricRegistry.counter(SAVED_METRIC_NAME).inc(savedCount)
    }

    private fun reportFailed(failedCount: Long) {
        metricRegistry.counter(FAILED_METRIC_NAME).inc(failedCount)
    }
}