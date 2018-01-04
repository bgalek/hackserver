package pl.allegro.experiments.chi.chiserver.application.experiments

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.MeasurementsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.JsonConverter
import pl.allegro.experiments.chi.chiserver.logger
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint

@RestController
@RequestMapping(value = ["/api/admin/experiments"], produces = [APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE])
class ExperimentsController(private val experimentsRepository: ExperimentsRepository,
                            private val measurementsRepository: MeasurementsRepository,
                            private val jsonConverter: JsonConverter) {

    companion object {
        private val logger by logger()
    }

    @MeteredEndpoint
    @GetMapping(path = arrayOf(""))
    fun allExperiments() : String {
        ExperimentsController.logger.info("All experiments request received")
        return experimentsRepository.all
            .let { measurementsRepository.withMeasurements(it) }
            .let { jsonConverter.toJson(it) }
    }

    @MeteredEndpoint
    @GetMapping(path = arrayOf("{experimentId}"))
    fun getExperiment(@PathVariable experimentId: String) : ResponseEntity<String> {
        ExperimentsController.logger.info("Single experiment request received")
        return experimentsRepository.getExperiment(experimentId)
            ?.let { measurementsRepository.withMeasurements(it) }
            ?.let { jsonConverter.toJson(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: (ResponseEntity(HttpStatus.NOT_FOUND))
    }
}
