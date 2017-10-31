package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import pl.allegro.experiments.chi.chiserver.assignments.ExperimentAssignment
import pl.allegro.experiments.chi.chiserver.assignments.ExperimentAssignmentRepository
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

class BufferedAssignmentRepository(
        private val metricReporter: MetricReporter,
        private val buffer: AssignmentBuffer,
        private val repository: ExperimentAssignmentRepository): ExperimentAssignmentRepository {

    companion object {
        private val logger = Logger.getLogger(BufferedAssignmentRepository::class.java.name)
    }

    override fun save(experimentAssignment: ExperimentAssignment) {
        if (buffer.isFull()) {
            val lostAssignments = buffer.flush()
            metricReporter.reportDropped((lostAssignments.size.toLong()))
            logger.warning("Buffer overloaded. Flushing buffer. Lost ${lostAssignments.size} assignments")
        }

        buffer.add(experimentAssignment)
    }

    fun flush() {
        if (buffer.isEmpty()) {
            return // nothing to save
        }

        val start = System.nanoTime()
        val assignments = buffer.flush()
        logger.info("Flushing ${assignments.size} assignments from buffer")
        val failedCounter = saveFromBuffer(assignments)
        metricReporter.reportSaved(assignments.size - failedCounter)
        metricReporter.reportFailed(failedCounter)
        val duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)
        logger.info("Flushed ${assignments.size}, took $duration ms")
    }

    private fun saveFromBuffer(assignments: List<ExperimentAssignment>): Long {
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
}