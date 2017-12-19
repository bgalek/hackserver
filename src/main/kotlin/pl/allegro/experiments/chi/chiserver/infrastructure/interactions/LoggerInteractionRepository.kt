package pl.allegro.experiments.chi.chiserver.infrastructure.interactions

import pl.allegro.experiments.chi.chiserver.domain.interactions.Interaction
import pl.allegro.experiments.chi.chiserver.domain.interactions.InteractionRepository
import java.util.logging.Logger

class LoggerInteractionRepository : InteractionRepository {

    companion object {
        private val logger = Logger.getLogger(LoggerInteractionRepository::class.java.name)
    }

    override fun save(interaction: Interaction) {
        logger.info("""
             userId: ${interaction.userId}
             userCmId: ${interaction.userCmId}
             experimentId: ${interaction.experimentId}
             variantName: ${interaction.variantName}
             internal: ${interaction.internal}
             deviceClass: ${interaction.deviceClass}
             interactionDate: ${interaction.interactionDate}
        """.trimIndent())
    }
}