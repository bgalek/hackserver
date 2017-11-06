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
        private val FLUSH_METRIC_NAME = "chi.server.experiments.interactions.kafka.flush"
        private val logger = Logger.getLogger(BufferedInteractionRepository::class.java.name)
    }

    override fun save(interaction: Interaction) {
        buffer.add(interaction)
    }

    fun flush() {
        val interactions = buffer.flush()
        saveToRepository(interactions)
    }

    private fun saveToRepository(interactions: List<Interaction>) {
        val summary = SaveSummary()

        interactions.stream().forEach { assignment ->
            try {
                repository.save(assignment)
                summary.reportSuccess()
            } catch (e: Exception) {
                summary.reportFailure(e)
            }
        }
        summary.reportAll()
    }

    private inner class SaveSummary(
            private var numberOfFailures: Long = 0,
            private var numberOfSuccessfullySaved: Long = 0) {
        private val start = System.currentTimeMillis()

        fun reportAll() {
            metricRegistry.meter(SAVED_METRIC_NAME).mark(numberOfSuccessfullySaved)
            metricRegistry.meter(FAILED_METRIC_NAME).mark(numberOfFailures)
            metricRegistry.timer(FLUSH_METRIC_NAME).update(start - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
        }

        fun reportSuccess() {
            numberOfSuccessfullySaved++
        }

        fun reportFailure(e : Exception) {
            logger.warning("Saving interaction failed, $e")
            numberOfSuccessfullySaved++
        }
    }
}