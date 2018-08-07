package pl.allegro.experiments.chi.chiserver.application.experiments;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.ClientExperiment;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = {"/api/experiments"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE})
class ClientExperimentsControllerV2 {
    final Gson jsonConverter;
    private final ExperimentGroupRepository experimentGroupRepository;

    private final ClientExperimentsControllerV3 controllerV3;

    private static final Logger logger = LoggerFactory.getLogger(ClientExperimentsControllerV2.class);

    ClientExperimentsControllerV2(ClientExperimentsControllerV3 controllerV3) {
        this.controllerV3 = controllerV3;
        this.jsonConverter = controllerV3.jsonConverter;
        this.experimentGroupRepository = controllerV3.experimentGroupRepository;
    }

    private boolean isV2Compliant(Experiment experiment) {
        return !experimentGroupRepository.experimentInGroup(experiment.getId());
    }

    Stream<Experiment> experimentStream() {
        return controllerV3.experimentStream().filter(this::isV2Compliant);
    }

    private ClientExperiment convertToClientExperiment(Experiment experiment) {
        return new ClientExperiment(experiment);
    }

    @MeteredEndpoint
    @GetMapping(path = {"/v2"})
    String experiments() {
        logger.debug("Client V2 experiments request received");
        return jsonConverter.toJson(experimentStream()
                .map(this::convertToClientExperiment)
                .collect(toList())
        );
    }
}
