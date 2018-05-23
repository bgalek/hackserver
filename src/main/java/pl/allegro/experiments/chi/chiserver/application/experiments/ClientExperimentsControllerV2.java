package pl.allegro.experiments.chi.chiserver.application.experiments;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.ClientExperiment;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.util.stream.Collectors;

@RestController
@RequestMapping(value = {"/api/experiments"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE})
class ClientExperimentsControllerV2 {
    private final ExperimentsRepository experimentsRepository;
    private final Gson jsonConverter;
    private final CrisisManagementFilter crisisManagementFilter;

    private static final Logger logger = LoggerFactory.getLogger(ClientExperimentsControllerV2.class);

    ClientExperimentsControllerV2(
            ExperimentsRepository experimentsRepository,
            Gson jsonConverter,
            CrisisManagementFilter crisisManagementFilter) {
        this.experimentsRepository = experimentsRepository;
        this.jsonConverter = jsonConverter;
        this.crisisManagementFilter = crisisManagementFilter;
    }

    @MeteredEndpoint
    @GetMapping(path = {"/v2", ""})
    String experiments() {
        logger.debug("Client V2 experiments request received");
        return jsonConverter.toJson(experimentsRepository
                .assignable()
                .stream()
                .filter(crisisManagementFilter::filter)
                .map(it -> new ClientExperiment(it))
                .collect(Collectors.toList()));
    }
}
