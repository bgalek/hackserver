package pl.allegro.experiments.chi.chiserver.application.experiments;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.MeasurementsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.PermissionsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.AuthorizationException;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentActions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentNotFoundException;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.audit.Auditor;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.audit.AuditLog;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create.ExperimentCreationRequest;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ProlongExperimentProperties;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.UpdateExperimentProperties;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.StartExperimentProperties;
import pl.allegro.tech.common.andamio.errors.Error;
import pl.allegro.tech.common.andamio.errors.ErrorsHolder;
import pl.allegro.tech.common.andamio.errors.SimpleErrorsHolder;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = {"/api/admin/experiments"}, produces = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
public class ExperimentsController {
    private final ExperimentsRepository experimentsRepository;
    private final MeasurementsRepository measurementsRepository;
    private final PermissionsRepository permissionsRepository;
    private ExperimentActions experimentActions;
    private final Gson jsonConverter;
    private final Auditor auditor;

    private static final Logger logger = LoggerFactory.getLogger(ExperimentsController.class);

    public ExperimentsController(
            ExperimentsRepository experimentsRepository,
            MeasurementsRepository measurementsRepository,
            PermissionsRepository permissionsRepository,
            ExperimentActions experimentActions,
            Gson jsonConverter,
            Auditor auditor) {
        this.experimentsRepository = experimentsRepository;
        this.measurementsRepository = measurementsRepository;
        this.permissionsRepository = permissionsRepository;
        this.experimentActions = experimentActions;
        this.jsonConverter = jsonConverter;
        this.auditor = auditor;
    }

    @MeteredEndpoint
    @GetMapping(path = {""})
    String allExperiments() {
        logger.info("All experiments request received");
        return jsonConverter.toJson(measurementsRepository.withMeasurements(experimentsRepository.getAll())
                .stream()
                .map(permissionsRepository::withPermissions)
                .collect(Collectors.toList()));
    }

    @MeteredEndpoint
    @GetMapping(path = {"{experimentId}"})
    ResponseEntity<String> getExperiment(@PathVariable String experimentId) {
        logger.info("Single experiment request received");
        return experimentsRepository.getExperiment(experimentId)
                .map(measurementsRepository::withMeasurements)
                .map(permissionsRepository::withPermissions)
                .map(e -> ResponseEntity.ok(jsonConverter.toJson(e)))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @MeteredEndpoint
    @PostMapping(path = {""})
    ResponseEntity<String> addExperiment(@RequestBody ExperimentCreationRequest experimentCreationRequest) {
        logger.info("Experiment creation request received", experimentCreationRequest);
        experimentActions.create(experimentCreationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @MeteredEndpoint
    @PutMapping(path = {"{experimentId}/start"})
    ResponseEntity<String> startExperiment(
            @PathVariable String experimentId,
            @RequestBody StartExperimentProperties properties) {
        logger.info("Start experiment request received: " + experimentId);
        experimentActions.start(experimentId, properties);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @MeteredEndpoint
    @PutMapping(path = {"{experimentId}/prolong"})
    ResponseEntity<String> prolongExperiment(
            @PathVariable String experimentId,
            @RequestBody ProlongExperimentProperties properties) {
        logger.info("Prolong experiment request received: " + experimentId);
        experimentActions.prolong(experimentId, properties);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @MeteredEndpoint
    @PutMapping(path = {"{experimentId}/update-descriptions"})
    ResponseEntity<String> updateExperimentDescriptions(
            @PathVariable String experimentId,
            @RequestBody UpdateExperimentProperties properties) {
        logger.info("Update experiment descriptions request received: " + experimentId);
        experimentActions.updateDescriptions(experimentId, properties);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @MeteredEndpoint
    @PutMapping(path = {"{experimentId}/stop"})
    ResponseEntity<String> stopExperiment(@PathVariable String experimentId) {
        logger.info("Stop experiment request received: " + experimentId);
        experimentActions.stop(experimentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @MeteredEndpoint
    @PutMapping(path = {"{experimentId}/pause"})
    ResponseEntity<String> pauseExperiment(@PathVariable String experimentId) {
        logger.info("Pause experiment request received: " + experimentId);
        experimentActions.pause(experimentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @MeteredEndpoint
    @PutMapping(path = {"{experimentId}/resume"})
    ResponseEntity<String> resumeExperiment(@PathVariable String experimentId) {
        logger.info("Resume experiment request received: " + experimentId);
        experimentActions.resume(experimentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @MeteredEndpoint
    @DeleteMapping(path = {"{experimentId}"})
    ResponseEntity<String> deleteExperiment(@PathVariable String experimentId) {
        logger.info("Delete experiment request received: " + experimentId);
        experimentActions.delete(experimentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @MeteredEndpoint
    @GetMapping(path = {"{experimentId}/audit-log"})
    ResponseEntity<String> getAuditLog(@PathVariable String experimentId) {
        logger.info("Audit log request received: " + experimentId);
        final AuditLog auditLog = auditor.getAuditLog(experimentId);
        final String body = jsonConverter.toJson(auditLog);
        return ResponseEntity.ok(body);
    }

    @ExceptionHandler(AuthorizationException.class)
    ResponseEntity<ErrorsHolder> handle(AuthorizationException exception) {
        logger.error("Command error: " + exception.getClass().getSimpleName() + "cause: " + exception.getMessage());
        Error error = Error.error()
                .fromException(exception)
                .withCode("AuthorizationException")
                .withMessage(exception.getMessage())
                .build();
        return new ResponseEntity<>(new SimpleErrorsHolder(error), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ExperimentNotFoundException.class)
    ResponseEntity<ErrorsHolder> handle(ExperimentNotFoundException exception) {
        return handleBadRequest(exception, exception.getClass().getSimpleName());
    }

    @ExceptionHandler(ExperimentCommandException.class)
    ResponseEntity<ErrorsHolder> handle(ExperimentCommandException exception) {
        return handleBadRequest(exception, exception.getClass().getSimpleName());
    }

    private ResponseEntity<ErrorsHolder> handleBadRequest(RuntimeException exception, String code) {
        logger.error("Command error: " + exception.getClass().getSimpleName() + "cause: " + exception.getMessage());
        Error error = Error.error()
                .fromException(exception)
                .withCode(code)
                .withMessage(exception.getMessage())
                .build();

        return new ResponseEntity<>(new SimpleErrorsHolder(error), HttpStatus.BAD_REQUEST);
    }
}
