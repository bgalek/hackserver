package pl.allegro.experiments.chi.chiserver.application.experiments

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.AuthorizationException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create.CreateExperimentCommandFactory
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.MeasurementsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.PermissionsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.*
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create.ExperimentCreationException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create.ExperimentCreationRequest
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete.DeleteExperimentCommandFactory
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete.DeleteExperimentException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start.StartExperimentCommandFactory
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start.StartExperimentException
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start.StartExperimentProperties
import pl.allegro.experiments.chi.chiserver.infrastructure.JsonConverter
import pl.allegro.experiments.chi.chiserver.logger
import pl.allegro.tech.common.andamio.errors.Error
import pl.allegro.tech.common.andamio.errors.ErrorsHolder
import pl.allegro.tech.common.andamio.errors.SimpleErrorsHolder
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint

@RestController
@RequestMapping(value = ["/api/admin/experiments"], produces = [APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE])
class ExperimentsController(private val experimentsRepository: ExperimentsRepository,
                            private val measurementsRepository: MeasurementsRepository,
                            private val createExperimentCommandFactory: CreateExperimentCommandFactory,
                            private val startExperimentCommandFactory: StartExperimentCommandFactory,
                            private val deleteExperimentCommandFactory: DeleteExperimentCommandFactory,
                            private val permissionsRepository: PermissionsRepository,
                            private val jsonConverter: JsonConverter) {

    companion object {
        private val logger by logger()
    }

    @MeteredEndpoint
    @GetMapping(path = [""])
    fun allExperiments(): String {
        logger.info("All experiments request received")
        return experimentsRepository.getAll()
                .let { measurementsRepository.withMeasurements(it)
                        .map { permissionsRepository.withPermissions(it)} }
                .let { jsonConverter.toJson(it) }
    }
    
    @MeteredEndpoint
    @GetMapping(path = ["{experimentId}"])
    fun getExperiment(@PathVariable experimentId: String): ResponseEntity<String> {
        logger.info("Single experiment request received")
        return experimentsRepository.getExperiment(experimentId)
                ?.let { measurementsRepository.withMeasurements(it) }
                ?.let { permissionsRepository.withPermissions(it) }
                ?.let { jsonConverter.toJson(it) }
                ?.let { ResponseEntity.ok(it) }
                ?: (ResponseEntity(HttpStatus.NOT_FOUND))
    }

    @MeteredEndpoint
    @PostMapping(path = [""])
    fun addExperiment(@RequestBody experimentCreationRequest: ExperimentCreationRequest): ResponseEntity<String> {
        logger.info("Experiment creation request received", experimentCreationRequest)
        createExperimentCommandFactory.createExperimentCommand(experimentCreationRequest).execute()
        return ResponseEntity(HttpStatus.CREATED)
    }

    @MeteredEndpoint
    @PutMapping(path = ["{experimentId}/start"])
    fun startExperiment(
            @PathVariable experimentId: String,
            @RequestBody properties: StartExperimentProperties): ResponseEntity<String> {
        logger.debug("Start experiment request received")
        startExperimentCommandFactory.startExperimentCommand(experimentId, properties).execute()
        return ResponseEntity(HttpStatus.OK)
    }

    @MeteredEndpoint
    @DeleteMapping(path = ["{experimentId}"])
    fun deleteExperiment(
            @PathVariable experimentId: String): ResponseEntity<String> {
        logger.debug("Delete experiment request received")
        deleteExperimentCommandFactory.deleteExperimentCommand(experimentId).execute()
        return ResponseEntity(HttpStatus.OK)
    }

    @ExceptionHandler(ExperimentCreationException::class)
    fun handle(exception: ExperimentCreationException): ResponseEntity<ErrorsHolder> {
        return handleBadRequest(exception, "ExperimentCreationException")
    }

    @ExceptionHandler(AuthorizationException::class)
    fun handle(exception: AuthorizationException): ResponseEntity<ErrorsHolder> {
        logger.error("Error creating experiment: ${exception.javaClass.simpleName}, cause: ${exception.message}")
        return ResponseEntity(SimpleErrorsHolder(Error.error().fromException(exception).withCode("AuthorizationException").withMessage(exception.message).build()), HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(ExperimentNotFoundException::class)
    fun handle(exception: ExperimentNotFoundException): ResponseEntity<ErrorsHolder> {
        return handleBadRequest(exception, "ExperimentNotFoundException")
    }

    @ExceptionHandler(StartExperimentException::class)
    fun handle(exception: StartExperimentException): ResponseEntity<ErrorsHolder> {
        return handleBadRequest(exception, "StartExperimentException")
    }

    @ExceptionHandler(DeleteExperimentException::class)
    fun handle(exception: DeleteExperimentException): ResponseEntity<ErrorsHolder> {
        return handleBadRequest(exception, "DeleteExperimentException")
    }

    fun handleBadRequest(exception: RuntimeException, code: String): ResponseEntity<ErrorsHolder> {
        logger.error("Error managing experiment: ${exception.javaClass.simpleName}, cause: ${exception.message}")
        return ResponseEntity(SimpleErrorsHolder(Error.error().fromException(exception)
                .withCode(code)
                .withMessage(exception.message)
                .build()), HttpStatus.BAD_REQUEST)
    }
}
