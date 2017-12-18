package pl.allegro.experiments.chi.chiserver.interactions.infrastructure

import pl.allegro.experiments.chi.chiserver.interactions.Interaction
import pl.allegro.experiments.chi.chiserver.interactions.InteractionRepository
import java.util.logging.Logger

class LoggerInteractionRepository : InteractionRepository {

    companion object {
        private val logger = Logger.getLogger(LoggerInteractionRepository::class.java.name)
    }

    override fun save(interaction: Interaction): Boolean {
        logger.info("""
             userId: ${interaction.userId}
             userCmId: ${interaction.userCmId}
             experimentId: ${interaction.experimentId}
             variantName: ${interaction.variantName}
             internal: ${interaction.internal}
             deviceClass: ${interaction.deviceClass}
             interactionDate: ${interaction.interactionDate}
        """.trimIndent())
        return true
    }
}