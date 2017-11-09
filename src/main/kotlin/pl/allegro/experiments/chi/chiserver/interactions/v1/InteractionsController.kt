package pl.allegro.experiments.chi.chiserver.interactions.v1

import com.codahale.metrics.MetricRegistry
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.allegro.experiments.chi.chiserver.interactions.InteractionRepository
import pl.allegro.experiments.chi.chiserver.logger
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint

@RestController
@RequestMapping(value = "/api/interactions", produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
class InteractionsController(
    private val interactionRepository: InteractionRepository,
    private val metricRegistry: MetricRegistry) {

    private val RECEIVED_INTERACTIONS = "interactions.received"

    companion object {
        private val logger by logger()
    }

    @MeteredEndpoint
    @PostMapping(path = arrayOf("/v1", ""))
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    fun saveInteractions(@RequestBody interactionsAsJson: String) {
        logger.debug("Save interactions request received")
        val interactions = InteractionConverter().fromJson(interactionsAsJson)

        metricRegistry.meter(RECEIVED_INTERACTIONS).mark(interactions.size.toLong())
        interactions.forEach { interaction ->
            interactionRepository.save(interaction)
        }
    }

    @ExceptionHandler(InvalidFormatException::class)
    fun invalidBody(): ResponseEntity<Unit> {
        logger.warn("Save interactions request was invalid")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
    }
}