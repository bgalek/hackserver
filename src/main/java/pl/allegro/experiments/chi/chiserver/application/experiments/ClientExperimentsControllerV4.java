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
import pl.allegro.experiments.chi.chiserver.infrastructure.ClientExperimentFactory;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = {"/api/experiments"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE})
class ClientExperimentsControllerV4 {
    final Gson jsonConverter;
    final ExperimentGroupRepository experimentGroupRepository;
    final ClientExperimentFactory clientExperimentFactory;

    private final ClientExperimentsControllerV5 controllerV5;
    private static final Logger logger = LoggerFactory.getLogger(ClientExperimentsControllerV4.class);

    ClientExperimentsControllerV4(ClientExperimentsControllerV5 controllerV5) {
        this.controllerV5 = controllerV5;
        this.jsonConverter = controllerV5.jsonConverter;
        this.experimentGroupRepository = controllerV5.experimentGroupRepository;
        this.clientExperimentFactory = controllerV5.clientExperimentFactory;
    }

    private boolean isV4Compliant(ExperimentDefinition experiment) {
        return !experiment.isFullOn();
    }

    Stream<ExperimentDefinition> experimentStream() {
        return controllerV5.experimentStream().filter(this::isV4Compliant);
    }

    @MeteredEndpoint
    @GetMapping(path = {"/v4"})
    String experiments() {
        logger.debug("Client V4 experiments request received");
        return jsonConverter.toJson(experimentStream()
                .map(clientExperimentFactory::clientExperiment)
                .collect(toList())
        );
    }
}
