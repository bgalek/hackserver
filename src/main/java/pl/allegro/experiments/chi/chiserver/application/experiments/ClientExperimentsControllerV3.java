package pl.allegro.experiments.chi.chiserver.application.experiments;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.ExperimentFactory;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;


import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = {"/api/experiments"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE})
class ClientExperimentsControllerV3 {
    final Gson jsonConverter;
    final ExperimentGroupRepository experimentGroupRepository;
    final ExperimentFactory experimentFactory;
    private final ClientExperimentsControllerV4 controllerV4;

    private static final Logger logger = LoggerFactory.getLogger(ClientExperimentsControllerV3.class);

    ClientExperimentsControllerV3(ClientExperimentsControllerV4 controllerV4) {
        this.controllerV4 = controllerV4;
        this.jsonConverter = controllerV4.jsonConverter;
        this.experimentGroupRepository = controllerV4.experimentGroupRepository;
        this.experimentFactory = controllerV4.experimentFactory;
    }

    private boolean isV3Compliant(ExperimentDefinition experiment) {
        return !experiment.hasCustomParam();
    }

    Stream<ExperimentDefinition> experimentStream() {
        return controllerV4.experimentStream().filter(this::isV3Compliant);
    }

    @MeteredEndpoint
    @GetMapping(path = {"/v3"})
    String experiments() {
        logger.debug("Client V3 experiments request received");
        return jsonConverter.toJson(experimentStream()
                .map(experimentFactory::clientExperiment)
                .collect(toList())
        );
    }
}
