package pl.allegro.experiments.chi.chiserver.application.experiments;

import com.google.gson.Gson;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.ClientExperiment;
import pl.allegro.experiments.chi.chiserver.infrastructure.ClientExperimentFactory;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.util.stream.Collectors;

@RestController
@RequestMapping(value = {"/api/experiments"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE})
class ClientExperimentsControllerV3 {
    private final ExperimentsRepository experimentsRepository;
    private final Gson jsonConverter;
    private final CrisisManagementFilter crisisManagementFilter;
    private final ExperimentGroupRepository experimentGroupRepository;
    private final ClientExperimentFactory clientExperimentFactory;

    ClientExperimentsControllerV3(
            ExperimentsRepository experimentsRepository,
            Gson jsonConverter,
            CrisisManagementFilter crisisManagementFilter,
            ExperimentGroupRepository experimentGroupRepository,
            ClientExperimentFactory clientExperimentFactory) {
        this.experimentsRepository = experimentsRepository;
        this.jsonConverter = jsonConverter;
        this.crisisManagementFilter = crisisManagementFilter;
        this.experimentGroupRepository = experimentGroupRepository;
        this.clientExperimentFactory = clientExperimentFactory;
    }

    @MeteredEndpoint
    @GetMapping(path = {"/v3", ""})
    String experiments() {
        return jsonConverter.toJson(experimentsRepository
                .assignable()
                .stream()
                .filter(crisisManagementFilter::filter)
                .map(it -> !experimentGroupRepository.experimentInGroup(it.getId())
                            ? new ClientExperiment(it) : clientExperimentFactory.fromGroupedExperiment(it.getDefinition().get()).get()
                )
                .collect(Collectors.toList()));
    }
}
