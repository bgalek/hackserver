package pl.allegro.experiments.chi.chiserver.interactions

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = "/api/interactions", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE))
class InteractionsController(
        private val interactionRepository: InteractionRepository) {

    @PostMapping(path = arrayOf("/v1", ""))
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    fun assignToExperiments(@RequestBody interactions: InteractionsDto) {
        interactions.interactionDtos
                .forEach { interaction -> interactionRepository.save(interaction.toInteraction()) }
    }
}