package pl.allegro.experiments.chi.chiserver.interactions.infrastructure

import com.codahale.metrics.MetricRegistry
import pl.allegro.experiments.chi.chiserver.interactions.Interaction
import pl.allegro.experiments.chi.chiserver.interactions.InteractionRepository
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

class BufferedInteractionRepository(
        private val metricRegistry: MetricRegistry,
        private val buffer: InteractionBuffer,
        private val repository: InteractionRepository): InteractionRepository {

    companion object {
        private val SAVED_METRIC_NAME = "chi.server.experiments.interactions.kafka.saved"
        private val FAILED_METRIC_NAME = "chi.server.experiments.interactions.kafka.failed"
        private val logger = Logger.getLogger(BufferedInteractionRepository::class.java.name)
    }

    override fun save(interaction: Interaction) {
        buffer.add(interaction)
    }

    fun flush() {
        val start = System.nanoTime()
        val interactions = buffer.flush()
        logger.info("Flushing ${interactions.size} interactions from buffer")
        val failedCounter = saveFromBuffer(interactions)
        reportSaved(interactions.size - failedCounter)
        reportFailed(failedCounter)
        val duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)
        logger.info("Flushed ${interactions.size}, took $duration ms")
    }

    private fun saveFromBuffer(interactions: List<Interaction>): Long {
        var failedCounter: Long = 0
        interactions.stream().forEach { assignment ->
            try {
                repository.save(assignment)
            } catch (e: CouldNotSendMessageToKafkaError) {
                logger.warning("Saving interaction failed, $e")
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