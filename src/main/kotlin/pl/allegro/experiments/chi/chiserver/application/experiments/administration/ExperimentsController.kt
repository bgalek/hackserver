package pl.allegro.experiments.chi.chiserver.application.experiments.administration

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.MeasurementsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.JsonConverter
import pl.allegro.experiments.chi.chiserver.logger
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint

@RestController
@RequestMapping(value = ["/api/admin/experiments"], produces = [APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE])
class ExperimentsController(private val experimentsRepository: ExperimentsRepository,
                            private val measurementsRepository: MeasurementsRepository,
                            private val createExperimentCommandFactory: CreateExperimentCommandFactory,
                            private val jsonConverter: JsonConverter) {

    companion object {
        private val logger by logger()
    }

    @MeteredEndpoint
    @GetMapping(path = [""])
    fun allExperiments() : String {
        logger.info("All experiments request received")
        return experimentsRepository.all
            .let { measurementsRepository.withMeasurements(it) }
            .let { jsonConverter.toJson(it) }
    }

    @MeteredEndpoint
    @GetMapping(path = ["{experimentId}"])
    fun getExperiment(@PathVariable experimentId: String) : ResponseEntity<String> {
        logger.info("Single experiment request received")
        return experimentsRepository.getExperiment(experimentId)
            ?.let { measurementsRepository.withMeasurements(it) }
            ?.let { jsonConverter.toJson(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: (ResponseEntity(HttpStatus.NOT_FOUND))
    }

    @MeteredEndpoint
    @PostMapping(path = [""])
    fun addExperiment(@RequestBody experimentCreationRequest: ExperimentCreationRequest) : ResponseEntity<String> {
        logger.info("Experiment creation request received", experimentCreationRequest)
        createExperimentCommandFactory.createExperimentCommand(experimentCreationRequest).execute()
        return ResponseEntity(HttpStatus.CREATED)
    }
}
