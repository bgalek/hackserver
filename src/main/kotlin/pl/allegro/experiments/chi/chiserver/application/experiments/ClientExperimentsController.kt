package pl.allegro.experiments.chi.chiserver.application.experiments

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.JsonConverter
import pl.allegro.experiments.chi.chiserver.logger
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint

@RestController
@RequestMapping(
    value = ["/api/experiments"],
    produces = [MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE]
)
class ClientExperimentsController(
    private val experimentsRepository: ExperimentsRepository,
    private val jsonConverter: JsonConverter
) {

    companion object {
        private val logger by logger()
    }

    @MeteredEndpoint
    @GetMapping(path = ["/v2", ""])
    fun experimentsForClient() : String {
        logger.info("Active experiments request received")
        return jsonConverter.toJson(experimentsRepository.overridable)
    }
}