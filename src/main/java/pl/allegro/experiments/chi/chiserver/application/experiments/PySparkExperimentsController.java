package pl.allegro.experiments.chi.chiserver.application.experiments;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.ClientExperimentFactory;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * To be removed together with Pyspark processing
 *
 * @deprecated
 */
@Deprecated
@RestController
@RequestMapping(value = {"/api/pyspark/experiments"}, produces = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
public class PySparkExperimentsController {
    private static final Logger logger = LoggerFactory.getLogger(PySparkExperimentsController.class);

    private final ExperimentsRepository experimentsRepository;
    private final Gson jsonConverter;
    private final ClientExperimentFactory clientExperimentFactory;

    @Autowired
    public PySparkExperimentsController(ExperimentsRepository experimentsRepository, Gson jsonConverter, ClientExperimentFactory clientExperimentFactory) {
        this.experimentsRepository = experimentsRepository;
        this.jsonConverter = jsonConverter;
        this.clientExperimentFactory = clientExperimentFactory;
    }

    @MeteredEndpoint
    @GetMapping(path = {""})
    String allExperiments() {
        logger.info("Get all experiments for PySpark request received");
        return jsonConverter.toJson(
                 experimentsRepository.getAll().stream()
                .map(clientExperimentFactory::adminExperiment)
                .collect(Collectors.toList()));
    }
}