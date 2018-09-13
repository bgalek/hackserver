package pl.allegro.experiments.chi.chiserver.application.experiments;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.ClientExperimentInfo;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Endpoint with quick info about experiments, used by Opbox Admin to suggest
 * experiment names and variants.
 */
@RestController
@RequestMapping(value = {"/api/experiments-info"},
                produces = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
public class ClientExperimentsInfoController {
    private final ExperimentsRepository experimentsRepository;
    private final Gson jsonConverter;

    public ClientExperimentsInfoController(ExperimentsRepository experimentsRepository, Gson jsonConverter) {
        this.experimentsRepository = experimentsRepository;
        this.jsonConverter = jsonConverter;
    }

    @MeteredEndpoint
    @GetMapping(path = {""})
    String experiments() {
        List<ClientExperimentInfo> response = experimentsRepository
            .assignable()
            .stream()
            .map(ClientExperimentInfo::new)
            .collect(toList());
        return jsonConverter.toJson(response);
    }
}
