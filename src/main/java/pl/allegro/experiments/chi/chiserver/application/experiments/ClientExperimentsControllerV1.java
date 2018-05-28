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
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;
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
    private final ExperimentGroupRepository experimentGroupRepository;

    private static final Logger logger = LoggerFactory.getLogger(ClientExperimentsControllerV1.class);

    public ClientExperimentsControllerV1(
            ExperimentsRepository experimentsRepository,
            Gson jsonConverterV1,
            CrisisManagementFilter crisisManagementFilter,
            ExperimentGroupRepository experimentGroupRepository) {
        this.experimentsRepository = experimentsRepository;
        this.jsonConverterV1 = jsonConverterV1;
        this.crisisManagementFilter = crisisManagementFilter;
        this.experimentGroupRepository = experimentGroupRepository;
    }

    @MeteredEndpoint
    @GetMapping(path = {"/v1"})
    String experiments() {
        logger.debug("Client V1 experiments request received");
        return jsonConverterV1.toJson(experimentsRepository
                .assignable()
                .stream()
                .filter(crisisManagementFilter::filter)
                .filter(experiment -> !experimentGroupRepository.experimentInGroup(experiment.getId()))
                .collect(Collectors.toList()));
    }
}
