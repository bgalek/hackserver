package pl.allegro.experiments.chi.chiserver.interactions.infrastructure

import com.codahale.metrics.MetricRegistry
import pl.allegro.experiments.chi.chiserver.interactions.Interaction
import pl.allegro.experiments.chi.chiserver.interactions.InteractionRepository
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
        val interactions = buffer.flush()
        saveToRepository(interactions).report()
    }

    private fun saveToRepository(interactions: List<Interaction>): SaveSummary {
        val timer = metricRegistry.timer(SAVED_METRIC_NAME).time()
        var failedCounter: Long = 0
        interactions.stream().forEach { assignment ->
            try {
                repository.save(assignment)
            } catch (e: Exception) {
                logger.warning("Saving interaction failed, $e")
                failedCounter++
            }
        }
        timer.close()
        return SaveSummary(failedCounter, interactions.size - failedCounter)
    }

    private inner class SaveSummary(
            private val numberOfFailures: Long,
            private val numberOfSuccessfullySaved: Long) {

        fun report() {
            reportSaved(numberOfSuccessfullySaved)
            reportFailed(numberOfFailures)
        }

        private fun reportSaved(savedCount: Long) {
            metricRegistry.meter(SAVED_METRIC_NAME).mark(savedCount)
        }

        private fun reportFailed(failedCount: Long) {
            metricRegistry.meter(FAILED_METRIC_NAME).mark(failedCount)
        }
    }
}