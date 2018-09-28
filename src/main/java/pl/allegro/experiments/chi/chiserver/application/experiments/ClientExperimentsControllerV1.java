package pl.allegro.experiments.chi.chiserver.application.experiments;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.infrastructure.ClientExperiment;
import pl.allegro.experiments.chi.chiserver.infrastructure.ClientExperimentFactory;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(
        value = {"/api/experiments"},
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE}
)
public class ClientExperimentsControllerV1 {
    private final Gson jsonConverter;
    private final ClientExperimentFactory clientExperimentFactory;
    private final ClientExperimentsControllerV2 controllerV2;

    private static final Logger logger = LoggerFactory.getLogger(ClientExperimentsControllerV1.class);

    public ClientExperimentsControllerV1(Gson jsonConverter, ClientExperimentFactory clientExperimentFactory, ClientExperimentsControllerV2 controllerV2) {
        this.jsonConverter = jsonConverter;
        this.clientExperimentFactory = clientExperimentFactory;
        this.controllerV2 = controllerV2;
    }

    private Stream<ExperimentDefinition> experimentStream() {
        return controllerV2.experimentStream();
    }

    @MeteredEndpoint
    @GetMapping(path = {"/v1"})
    String experiments() {
        logger.debug("Client V1 experiments request received");

        return jsonConverter.toJson(experimentStream()
                .map(clientExperimentFactory::clientExperiment)
                .map(it -> mapToSuperOldFormat(it))
                .collect(toList())
        );
    }

    private Object mapToSuperOldFormat(ClientExperiment experiment) {
        Map map = new HashMap<>();
        if (experiment.getActivityPeriod() != null) {
            map.put("activeFrom", experiment.getActivityPeriod().getActiveFrom());
            map.put("activeTo", experiment.getActivityPeriod().getActiveTo());
        }
        map.put("owner", "Root");
        map.put("reportingEnabled", true);
        map.put("id", experiment.getId());
        map.put("variants", experiment.getVariants());
        return map;
    }
}
