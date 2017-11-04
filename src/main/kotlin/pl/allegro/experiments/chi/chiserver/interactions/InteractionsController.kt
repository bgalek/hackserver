package pl.allegro.experiments.chi.chiserver.interactions

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = "/api/interactions", produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
class InteractionsController(
        private val interactionRepository: InteractionRepository) {

    @PostMapping(path = arrayOf("/v1", ""))
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    fun assignToExperiments(@RequestBody interactionsAsJson: String) {
        val interactions = InteractionConverter().fromJson(interactionsAsJson)
        interactions.forEach { interaction ->
            interactionRepository.save(interaction)
        }
    }

    @ExceptionHandler(InvalidFormatException::class)
    fun invalidBody(): ResponseEntity<Unit> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
    }
}