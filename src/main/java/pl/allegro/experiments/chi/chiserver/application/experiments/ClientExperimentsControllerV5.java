package pl.allegro.experiments.chi.chiserver.application.experiments;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.ExperimentFactory;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = {"/api/experiments"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE})
class ClientExperimentsControllerV5 {
    final Gson jsonConverter;
    final ExperimentGroupRepository experimentGroupRepository;
    final ExperimentFactory experimentFactory;

    private final ExperimentsRepository experimentsRepository;
    private final CrisisManagementFilter crisisManagementFilter;
    private static final Logger logger = LoggerFactory.getLogger(ClientExperimentsControllerV5.class);

    ClientExperimentsControllerV5(
            ExperimentsRepository experimentsRepository,
            Gson jsonConverter,
            CrisisManagementFilter crisisManagementFilter,
            ExperimentGroupRepository experimentGroupRepository,
            ExperimentFactory experimentFactory) {
        this.experimentsRepository = experimentsRepository;
        this.jsonConverter = jsonConverter;
        this.crisisManagementFilter = crisisManagementFilter;
        this.experimentGroupRepository = experimentGroupRepository;
        this.experimentFactory = experimentFactory;
    }

    Stream<ExperimentDefinition> experimentStream() {
        return experimentsRepository
                .assignable()
                .stream()
                .filter(crisisManagementFilter::filter);
    }

    @MeteredEndpoint
    @GetMapping(path = {"/v5", ""})
    String experiments() {
        logger.debug("Client V5 experiments request received");
        return jsonConverter.toJson(experimentStream()
                .map(experimentFactory::clientExperiment)
                .collect(toList())
        );
    }
}
