package pl.allegro.experiments.chi.chiserver.application.interactions.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.allegro.experiments.chi.chiserver.domain.interactions.Interaction;
import pl.allegro.experiments.chi.chiserver.domain.interactions.InteractionRepository;
import pl.allegro.experiments.chi.chiserver.domain.interactions.InteractionsFactory;
import pl.allegro.experiments.chi.chiserver.infrastructure.interactions.InteractionsMetricsReporter;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.util.List;

@RestController
@RequestMapping(value = {"/api/interactions"}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
class InteractionsController {

    private final InteractionRepository interactionRepository;
    private final InteractionsFactory interactionsFactory;
    private final InteractionsMetricsReporter interactionsMetricsReporter;

    private static final Logger logger = LoggerFactory.getLogger(InteractionsController.class);

    InteractionsController(
            InteractionRepository interactionRepository,
            InteractionsFactory interactionsFactory,
            InteractionsMetricsReporter interactionsMetricsReporter) {
        this.interactionRepository = interactionRepository;
        this.interactionsFactory = interactionsFactory;
        this.interactionsMetricsReporter = interactionsMetricsReporter;
    }

    @MeteredEndpoint
    @PostMapping(path = {"/v1", ""})
    void saveInteractions(@RequestBody String interactionsAsJson) {
        logger.debug("Save interactions request received");
        List<Interaction> interactions = interactionsFactory.fromJson(interactionsAsJson);

        interactions.forEach(interactionRepository::save);

        interactionsMetricsReporter.meterAccepted(interactions);
    }

    @ExceptionHandler(InvalidFormatException.class)
    ResponseEntity invalidBody(InvalidFormatException e) {
        logger.warn("Save interactions request was invalid", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
}