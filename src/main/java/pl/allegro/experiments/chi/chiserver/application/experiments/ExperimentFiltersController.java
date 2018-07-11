package pl.allegro.experiments.chi.chiserver.application.experiments;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.ExperimentFactory;
import pl.allegro.tech.common.andamio.endpoint.PublicEndpoint;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
public class ExperimentFiltersController {
    private static final Logger logger = LoggerFactory.getLogger(ExperimentFiltersController.class);

    private final ExperimentsRepository experimentsRepository;
    private final Gson jsonConverter;
    private final ExperimentFactory clientExperimentFactory;

    public ExperimentFiltersController(
            ExperimentsRepository experimentsRepository,
            Gson jsonConverter,
            ExperimentFactory experimentFactory) {
        this.experimentsRepository = experimentsRepository;
        this.jsonConverter = jsonConverter;
        this.clientExperimentFactory = experimentFactory;
    }

    @MeteredEndpoint
    @GetMapping(path = {"/experiments/filters"}, produces = {APPLICATION_JSON_UTF8_VALUE})
    @PublicEndpoint(timeout = "3000")
    String allExperimentFilters() {
        logger.info("All experiment filters request received");
        return jsonConverter.toJson(
                experimentsRepository.getAll().stream()
                        .map(clientExperimentFactory::adminExperiment)
                        .filter(it -> !it.getEventDefinitions().isEmpty() && it.isActive())
                        .collect(Collectors.toMap(it -> it.getId(), it -> it.getEventDefinitions())));
    }
}
