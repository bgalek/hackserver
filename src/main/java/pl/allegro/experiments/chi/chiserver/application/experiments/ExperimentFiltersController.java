package pl.allegro.experiments.chi.chiserver.application.experiments;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.util.Collections;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Deprecated
@RestController
@RequestMapping(value = {"/api/admin/experiment-filters"}, produces = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
public class ExperimentFiltersController {
    private static final Logger logger = LoggerFactory.getLogger(ExperimentFiltersController.class);

    private final Gson jsonConverter;
    private final DummyFiltersProvider dummyFiltersProvider = new DummyFiltersProvider();

    public ExperimentFiltersController(Gson jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    /**
     * TODO this is a POC
     */
    @MeteredEndpoint
    @GetMapping(path = "")
    String experimentsWithFilters() {
        logger.info("experiments-with-filters request received");

        return jsonConverter.toJson( dummyFiltersProvider.provide() );
    }
}
