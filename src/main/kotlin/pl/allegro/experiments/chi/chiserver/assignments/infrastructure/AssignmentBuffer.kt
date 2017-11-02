package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import com.codahale.metrics.MetricRegistry
import pl.allegro.experiments.chi.chiserver.assignments.Assignment
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.logging.Logger

class AssignmentBuffer(
        private val maxSize: Int,
        private val metricRegistry: MetricRegistry) {

    private val queue: ConcurrentLinkedQueue<Assignment> = ConcurrentLinkedQueue()

    companion object {
        private val DROPPED_METRIC_NAME = "chi.server.experiments.assignments.kafka.dropped"
        private val logger = Logger.getLogger(AssignmentBuffer::class.java.name)
    }

    fun add(assignment: Assignment) {
        if (isFull()) {
            val dropped = flush()
            reportDropped(dropped.size.toLong())
            logger.warning("Buffer overloaded. Flushing buffer. Lost ${dropped.size} assignments")
        }
        queue.add(assignment)
    }

    fun flush(): List<Assignment> {
        synchronized(this) {
            val result: MutableList<Assignment> = LinkedList()
            while (!queue.isEmpty() && result.size <= maxSize) {
                result.add(queue.poll())
            }
            return result
        }
    }

    private fun isFull(): Boolean {
        synchronized(this) {
            return queue.size >= maxSize
        }
    }

    private fun reportDropped(droppedCount: Long) {
        metricRegistry.counter(DROPPED_METRIC_NAME).inc(droppedCount)
    }
}