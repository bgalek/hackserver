package pl.allegro.experiments.chi.chiserver.application.experiments;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.util.stream.Collectors;

@RestController
@RequestMapping(
        value = {"/api/experiments"},
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE}
)
public class ClientExperimentsControllerV1 {
    private final ExperimentsRepository experimentsRepository;
    private final Gson jsonConverterV1;
    private final CrisisManagementFilter crisisManagementFilter;

    private static final Logger logger = LoggerFactory.getLogger(ClientExperimentsControllerV1.class);

    public ClientExperimentsControllerV1(
            ExperimentsRepository experimentsRepository,
            Gson jsonConverterV1,
            CrisisManagementFilter crisisManagementFilter) {
        this.experimentsRepository = experimentsRepository;
        this.jsonConverterV1 = jsonConverterV1;
        this.crisisManagementFilter = crisisManagementFilter;
    }

    @MeteredEndpoint
    @GetMapping(path = {"/v1"})
    String experiments() {
        logger.info("Client V1 experiments request received");
        return jsonConverterV1.toJson(experimentsRepository
                .assignable()
                .stream()
                .filter(crisisManagementFilter::filter).collect(Collectors.toList()));
    }
}
