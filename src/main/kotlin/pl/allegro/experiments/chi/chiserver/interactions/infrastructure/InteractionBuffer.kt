package pl.allegro.experiments.chi.chiserver.interactions.infrastructure

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.annotation.Metered
import pl.allegro.experiments.chi.chiserver.interactions.Interaction
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.logging.Logger

class InteractionBuffer(
        private val maxSize: Int,
        private val metricRegistry: MetricRegistry) {

    private val queue: ConcurrentLinkedQueue<Interaction> = ConcurrentLinkedQueue()

    companion object {
        private val DROPPED_METRIC_NAME = "chi.server.experiments.interactions.kafka.dropped"
        private val logger = Logger.getLogger(InteractionBuffer::class.java.name)
    }

    fun add(interaction: Interaction) {
        if (isFull()) {
            val dropped = flush()
            reportDropped(dropped.size.toLong())
            logger.warning("Buffer overloaded. Flushing buffer. Lost ${dropped.size} interactions")
        }
        queue.add(interaction)
    }

    fun flush(): List<Interaction> {
        synchronized(this) {
            val result: MutableList<Interaction> = LinkedList()
            while (!queue.isEmpty() && result.size <= maxSize) {
                result.add(queue.poll())
            }
            return result
        }
    }

    private fun isFull(): Boolean {
        return queue.size >= maxSize
    }

    private fun reportDropped(droppedCount: Long) {
        metricRegistry.counter(DROPPED_METRIC_NAME).inc(droppedCount)
    }
}