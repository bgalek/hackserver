package pl.allegro.experiments.chi.chiserver.interactions.v1

import com.codahale.metrics.MetricRegistry
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.allegro.experiments.chi.chiserver.interactions.InteractionRepository
import pl.allegro.experiments.chi.chiserver.logger

@RestController
@RequestMapping(value = "/api/interactions", produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
class InteractionsController(
        private val interactionRepository: InteractionRepository,
        private val metricRegistry: MetricRegistry) {

    companion object {
        private val logger by logger()
        private val RECEIVED_INTERACTIONS = "chi.server.experiments.interactions.received"
    }

    @PostMapping(path = arrayOf("/v1", ""))
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    fun saveInteractions(@RequestBody interactionsAsJson: String) {
        logger.info("Save interactions request received")
        val interactions = InteractionConverter().fromJson(interactionsAsJson)

        metricRegistry.meter(RECEIVED_INTERACTIONS).mark()
        interactions.forEach { interaction ->
            interactionRepository.save(interaction)
        }
    }

    @ExceptionHandler(InvalidFormatException::class)
    fun invalidBody(): ResponseEntity<Unit> {
        logger.info("Save interactions request was invalid")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
    }
}