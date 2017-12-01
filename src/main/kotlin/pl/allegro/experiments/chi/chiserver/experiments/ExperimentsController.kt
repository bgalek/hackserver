package pl.allegro.experiments.chi.chiserver.experiments

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.allegro.experiments.chi.chiserver.infrastructure.JsonConverter
import pl.allegro.experiments.chi.chiserver.logger
import pl.allegro.experiments.chi.core.ExperimentsRepository
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint

@RestController
@RequestMapping(value = "/api", produces = arrayOf(APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE))
class ExperimentsController(private val experimentsRepository: ExperimentsRepository,
                            private val jsonConverter: JsonConverter) {

    companion object {
        private val logger by logger()
    }

    @MeteredEndpoint
    @GetMapping(path = arrayOf("/experiments/v1", "/experiments"))
    fun activeExperiments() : String {
        ExperimentsController.logger.info("Active experiments request received")
        return jsonConverter.toJSON(experimentsRepository.all)
    }

    @MeteredEndpoint
    @GetMapping(path = arrayOf("/admin/experiments/{experimentId}"))
    fun getExperiment(@PathVariable experimentId: String) : ResponseEntity<String> {
        ExperimentsController.logger.info("Single experiment request received")
        return experimentsRepository.getExperiment(experimentId)
                .map(jsonConverter::toJSON)
                .map { e -> ResponseEntity.ok(e) }
                .orElse(ResponseEntity(HttpStatus.NOT_FOUND))
    }
}
