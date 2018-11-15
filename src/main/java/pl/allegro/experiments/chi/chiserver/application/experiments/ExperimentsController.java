package pl.allegro.experiments.chi.chiserver.application.experiments;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.*;
import pl.allegro.experiments.chi.chiserver.domain.calculator.SampleSizeCalculator;
import pl.allegro.experiments.chi.chiserver.domain.calculator.SampleSizeCalculatorRequest;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentTagRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.MeasurementsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.audit.AuditLog;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.audit.Auditor;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianChartsRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicStatisticsForVariantMetricRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.ClientExperimentFactory;
import pl.allegro.tech.common.andamio.errors.Error;
import pl.allegro.tech.common.andamio.errors.ErrorsHolder;
import pl.allegro.tech.common.andamio.errors.SimpleErrorsHolder;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = {"/api/admin/experiments"}, produces = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
public class ExperimentsController {
    private static final Logger logger = LoggerFactory.getLogger(ExperimentsController.class);

    private final SampleSizeCalculator sampleSizeCalculator;
    private final ExperimentsRepository experimentsRepository;
    private final MeasurementsRepository measurementsRepository;
    private final BayesianChartsRepository bayesianChartsRepository;
    private ExperimentActions experimentActions;
    private final Gson jsonConverter;
    private final Auditor auditor;
    private final ExperimentGroupRepository experimentGroupRepository;
    private final ExperimentTagRepository experimentTagRepository;
    private final ClientExperimentFactory clientExperimentFactory;
    private final ClassicStatisticsForVariantMetricRepository classicStatisticsForVariantMetricRepository;

    public ExperimentsController(
            ExperimentTagRepository experimentTagRepository,
            SampleSizeCalculator sampleSizeCalculator,
            ExperimentsRepository experimentsRepository,
            MeasurementsRepository measurementsRepository,
            ExperimentActions experimentActions,
            Gson jsonConverter,
            Auditor auditor,
            BayesianChartsRepository bayesianChartsRepository,
            ExperimentGroupRepository experimentGroupRepository,
            ClientExperimentFactory clientExperimentFactory,
            ClassicStatisticsForVariantMetricRepository classicStatisticsForVariantMetricRepository) {
        this.experimentTagRepository = experimentTagRepository;
        this.sampleSizeCalculator = sampleSizeCalculator;
        this.experimentsRepository = experimentsRepository;
        this.measurementsRepository = measurementsRepository;
        this.experimentActions = experimentActions;
        this.jsonConverter = jsonConverter;
        this.auditor = auditor;
        this.bayesianChartsRepository = bayesianChartsRepository;
        this.experimentGroupRepository = experimentGroupRepository;
        this.clientExperimentFactory = clientExperimentFactory;
        this.classicStatisticsForVariantMetricRepository = classicStatisticsForVariantMetricRepository;
    }

    @MeteredEndpoint
    @GetMapping(path = {""})
    String allExperiments() {
        logger.info("All experiments request received");
        return jsonConverter.toJson(
                 experimentsRepository.getAll().stream()
                .map(clientExperimentFactory::adminExperiment)
                .map(it -> it.withMeasurements(measurementsRepository.getMeasurements(it.getId())))
                .map(it -> it.withHorizontalEqualizer(
                        bayesianChartsRepository.getHorizontalEqualizer(it.getId()).stream()
                                .filter(eq -> eq.getMetadata().getDeviceClass().equals(DeviceClass.all))
                                .findFirst()
                                .orElse(null)))
                .map(it -> experimentGroupRepository.findByExperimentId(it.getId())
                        .map(g -> it.withExperimentGroup(g))
                        .orElse(it))
                .collect(Collectors.toList()));
    }

    @MeteredEndpoint
    @GetMapping(path = "{experimentId}")
    ResponseEntity<String> getExperiment(@PathVariable String experimentId) {
        logger.info("AdminExperiment request received, experimentId:{}", experimentId);
        return experimentsRepository.getExperiment(experimentId)
                .map(clientExperimentFactory::adminExperiment)
                .map(it -> it.withMeasurements(measurementsRepository.getMeasurements(it.getId())))
                .map(it -> experimentGroupRepository.findByExperimentId(it.getId())
                        .map(g -> it.withExperimentGroup(g))
                        .orElse(it))
                .map(it -> it.withBonferroniCorrection(classicStatisticsForVariantMetricRepository.countNumberOfTests(experimentId)))
                .map(e -> ResponseEntity.ok(jsonConverter.toJson(e)))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @MeteredEndpoint
    @PostMapping(path = "")
    ResponseEntity<String> addExperiment(@RequestBody ExperimentCreationRequest experimentCreationRequest) {
        logger.info("Experiment creation request received", experimentCreationRequest);
        experimentActions.create(experimentCreationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @MeteredEndpoint
    @PostMapping(path = "/tags")
    ResponseEntity<String> createExperimentTag(
            @RequestBody ExperimentTagCreationRequest experimentTagCreationRequest) {
        logger.info("Experiment tag creation request received", experimentTagCreationRequest);
        experimentActions.createExperimentTag(experimentTagCreationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @MeteredEndpoint
    @GetMapping(path = "/tags")
    String allExperimentTags() {
        logger.info("Get all experiment tags request received");
        return jsonConverter.toJson(experimentTagRepository.all());
    }

    @MeteredEndpoint
    @PutMapping(path = "{experimentId}/start")
    ResponseEntity<String> startExperiment(
            @PathVariable String experimentId,
            @RequestBody StartExperimentProperties properties) {
        logger.info("Start experiment request received: " + experimentId);
        experimentActions.start(experimentId, properties);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @MeteredEndpoint
    @PutMapping(path = "{experimentId}/prolong")
    ResponseEntity<String> prolongExperiment(
            @PathVariable String experimentId,
            @RequestBody ProlongExperimentProperties properties) {
        logger.info("Prolong experiment request received: " + experimentId);
        experimentActions.prolong(experimentId, properties);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @MeteredEndpoint
    @PutMapping(path = "{experimentId}/update-descriptions")
    ResponseEntity<String> updateExperimentDescriptions(
            @PathVariable String experimentId,
            @RequestBody UpdateExperimentProperties properties) {
        logger.info("Update experiment descriptions request received: " + experimentId);
        experimentActions.updateDescriptions(experimentId, properties);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @MeteredEndpoint
    @PutMapping(path = "{experimentId}/update-variants")
    ResponseEntity<String> updateExperimentVariants(
            @PathVariable String experimentId,
            @RequestBody UpdateVariantsProperties properties) {
        logger.info("Update experiment variants request received: {}", experimentId, properties);
        experimentActions.updateVariants(experimentId, properties);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @MeteredEndpoint
    @PutMapping(path = "{experimentId}/update-event-definitions")
    ResponseEntity<String> updateExperimentEventDefinitions(
            @PathVariable String experimentId,
            @RequestBody List<EventDefinition> eventDefinitions) {
        logger.info("Update experiment event definitions request received: {}", experimentId, eventDefinitions);
        experimentActions.updateExperimentEventDefinitions(experimentId, eventDefinitions);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @MeteredEndpoint
    @PutMapping(path = "{experimentId}/stop")
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
    @PutMapping(path = "{experimentId}/resume")
    ResponseEntity<String> resumeExperiment(@PathVariable String experimentId) {
        logger.info("Resume experiment request received: " + experimentId);
        experimentActions.resume(experimentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @MeteredEndpoint
    @PutMapping(path = "{experimentId}/full-on")
    ResponseEntity<String> makeExperimentFullOn(
            @PathVariable String experimentId,
            @RequestBody MakeExperimentFullOnProperties properties) {
        logger.info("Make experiment full-on request received: " + experimentId);
        experimentActions.makeExperimentFullOn(experimentId, properties);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @MeteredEndpoint
    @DeleteMapping(path = "{experimentId}")
    ResponseEntity<String> deleteExperiment(@PathVariable String experimentId) {
        logger.info("Delete experiment request received: " + experimentId);
        experimentActions.delete(experimentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @MeteredEndpoint
    @GetMapping(path = "{experimentId}/audit-log")
    ResponseEntity<String> getAuditLog(@PathVariable String experimentId) {
        logger.info("Audit log request received: " + experimentId);
        final AuditLog auditLog = auditor.getAuditLog(experimentId);
        final String body = jsonConverter.toJson(auditLog);
        return ResponseEntity.ok(body);
    }

    @MeteredEndpoint
    @PostMapping(path = "groups")
    ResponseEntity<String> addExperimentToGroup(
            @RequestBody AddExperimentToGroupRequest addExperimentToGroupRequest) {
        logger.info("Add experiment to group request received, groupId:{}, expId:{}",
                addExperimentToGroupRequest.getId(), addExperimentToGroupRequest.getExperimentId());
        experimentActions.addExperimentToGroup(addExperimentToGroupRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @MeteredEndpoint
    @GetMapping(path = "groups/{groupId}")
    ResponseEntity<String> getExperimentGroup(@PathVariable String groupId) {
        logger.info("Single experiment group request received");
        return experimentGroupRepository.findById(groupId)
                .map(jsonConverter::toJson)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @MeteredEndpoint
    @GetMapping(path = "groups")
    String getExperimentGroups() {
        logger.info("All experiment group request received");
        return jsonConverter.toJson(
                experimentGroupRepository.findAll().stream().sorted().collect(Collectors.toList())
        );
    }

    @MeteredEndpoint
    @GetMapping(path = "calculate-sample-size")
    long calculateSampleSize(@ModelAttribute SampleSizeCalculatorRequest request) {
        logger.info("CalculateSampleSize request received - {}", request);

        return sampleSizeCalculator.calculateSampleSize(request);
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

    @ExceptionHandler(HttpMessageConversionException.class)
    ResponseEntity<ErrorsHolder> handle(HttpMessageConversionException exception) {
        return handleBadRequest(exception, exception.getClass().getSimpleName());
    }

    @ExceptionHandler(ExperimentCommandException.class)
    ResponseEntity<ErrorsHolder> handle(ExperimentCommandException exception) {
        return handleBadRequest(exception, exception.getClass().getSimpleName());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ErrorsHolder> handle(IllegalArgumentException exception) {
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