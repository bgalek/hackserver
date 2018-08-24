package pl.allegro.experiments.chi.chiserver.application.experiments;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(
        value = {"/api/experiments"},
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE}
)
public class ClientExperimentsControllerV1 {
    private final Gson jsonConverterV1;
    private final ClientExperimentsControllerV2 controllerV2;

    private static final Logger logger = LoggerFactory.getLogger(ClientExperimentsControllerV1.class);

    ClientExperimentsControllerV1(ClientExperimentsControllerV2 controllerV2, Gson jsonConverterV1) {
        this.controllerV2 = controllerV2;
        this.jsonConverterV1 = jsonConverterV1;
    }

    private Stream<ExperimentDefinition> experimentStream() {
        return controllerV2.experimentStream();
    }

    @MeteredEndpoint
    @GetMapping(path = {"/v1"})
    String experiments() {
        logger.debug("Client V1 experiments request received");
        return jsonConverterV1.toJson(experimentStream().collect(Collectors.toList()));
    }
}
